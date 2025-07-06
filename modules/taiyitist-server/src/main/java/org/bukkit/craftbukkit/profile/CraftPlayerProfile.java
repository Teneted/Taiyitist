package org.bukkit.craftbukkit.profile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.item.component.ResolvableProfile;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.configuration.ConfigSerializationUtil;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.ApiStatus.Internal;

@SerializableAs("PlayerProfile")
public final class CraftPlayerProfile implements PlayerProfile {
   private final UUID uniqueId;
   private final String name;
   private final PropertyMap properties;
   private final CraftPlayerTextures textures;

   @Nonnull
   public static GameProfile validateSkullProfile(@Nonnull GameProfile gameProfile) {
      boolean isValidSkullProfile = gameProfile.getName() != null || gameProfile.getProperties().containsKey("textures");
      Preconditions.checkArgument(isValidSkullProfile, "The skull profile is missing a name or textures!");
      return gameProfile;
   }

   @Nonnull
   public static ResolvableProfile validateSkullProfile(@Nonnull ResolvableProfile resolvableProfile) {
      boolean isValidSkullProfile = resolvableProfile.name().isPresent() || resolvableProfile.id().isPresent() || resolvableProfile.properties().containsKey("textures");
      Preconditions.checkArgument(isValidSkullProfile, "The skull profile is missing a name or textures!");
      return resolvableProfile;
   }

   @Nullable
   public static Property getProperty(@Nonnull GameProfile profile, String propertyName) {
      return (Property)Iterables.getFirst(profile.getProperties().get(propertyName), (Object)null);
   }

   private CraftPlayerProfile(UUID uniqueId, String name, boolean applyPreconditions) {
      this.properties = new PropertyMap();
      this.textures = new CraftPlayerTextures(this);
      if (applyPreconditions) {
         Preconditions.checkArgument(uniqueId != null || !StringUtils.isBlank(name), "uniqueId is null or name is blank");
      }

      this.uniqueId = uniqueId;
      this.name = name;
   }

   public CraftPlayerProfile(UUID uniqueId, String name) {
      this(uniqueId, name, true);
   }

   @Internal
   public CraftPlayerProfile(@Nonnull ResolvableProfile resolvableProfile) {
      this((UUID)resolvableProfile.id().orElse((UUID) null), (String)resolvableProfile.name().orElse((String) null), false);
      this.properties.putAll(resolvableProfile.properties());
   }

   public CraftPlayerProfile(@Nonnull GameProfile gameProfile) {
      this(gameProfile.getId(), gameProfile.getName());
      this.properties.putAll(gameProfile.getProperties());
   }

   private CraftPlayerProfile(@Nonnull CraftPlayerProfile other) {
      this(other.uniqueId, other.name);
      this.properties.putAll(other.properties);
      this.textures.copyFrom(other.textures);
   }

   public UUID getUniqueId() {
      return Objects.equals(this.uniqueId, Util.NIL_UUID) ? null : this.uniqueId;
   }

   public String getName() {
      return StringUtils.isBlank(this.name) ? null : this.name;
   }

   @Nullable
   Property getProperty(String propertyName) {
      return (Property)Iterables.getFirst(this.properties.get(propertyName), (Object)null);
   }

   void setProperty(String propertyName, @Nullable Property property) {
      this.removeProperty(propertyName);
      if (property != null) {
         this.properties.put(property.name(), property);
      }

   }

   void removeProperty(String propertyName) {
      this.properties.removeAll(propertyName);
   }

   void rebuildDirtyProperties() {
      this.textures.rebuildPropertyIfDirty();
   }

   public CraftPlayerTextures getTextures() {
      return this.textures;
   }

   public void setTextures(@Nullable PlayerTextures textures) {
      if (textures == null) {
         this.textures.clear();
      } else {
         this.textures.copyFrom(textures);
      }

   }

   public boolean isComplete() {
      return this.getUniqueId() != null && this.getName() != null && !this.textures.isEmpty();
   }

   public CompletableFuture<PlayerProfile> update() {
      return CompletableFuture.supplyAsync(this::getUpdatedProfile, Util.backgroundExecutor());
   }

   private CraftPlayerProfile getUpdatedProfile() {
      DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
      GameProfile profile = this.buildGameProfile();
      if (profile.getId().equals(Util.NIL_UUID)) {
         profile = (GameProfile)server.getProfileCache().get(profile.getName()).orElse(profile);
      }

      if (!profile.getId().equals(Util.NIL_UUID)) {
         ProfileResult newProfile = server.getSessionService().fetchProfile(profile.getId(), true);
         if (newProfile != null) {
            profile = newProfile.profile();
         }
      }

      return new CraftPlayerProfile(profile);
   }

   @Nonnull
   public ResolvableProfile buildResolvableProfile() {
      this.rebuildDirtyProperties();
      return new ResolvableProfile(Optional.ofNullable(this.name), Optional.ofNullable(this.uniqueId), this.properties);
   }

   @Nonnull
   public GameProfile buildGameProfile() {
      this.rebuildDirtyProperties();
      GameProfile profile = new GameProfile(this.uniqueId, this.name);
      profile.getProperties().putAll(this.properties);
      return profile;
   }

   public String toString() {
      this.rebuildDirtyProperties();
      StringBuilder builder = new StringBuilder();
      builder.append("CraftPlayerProfile [uniqueId=");
      builder.append(this.uniqueId);
      builder.append(", name=");
      builder.append(this.name);
      builder.append(", properties=");
      builder.append(toString(this.properties));
      builder.append("]");
      return builder.toString();
   }

   private static String toString(@Nonnull PropertyMap propertyMap) {
      StringBuilder builder = new StringBuilder();
      builder.append("{");
      propertyMap.asMap().forEach((propertyName, properties) -> {
         builder.append(propertyName);
         builder.append("=");
         builder.append((String)properties.stream().map(CraftProfileProperty::toString).collect(Collectors.joining(",", "[", "]")));
      });
      builder.append("}");
      return builder.toString();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof CraftPlayerProfile) {
         CraftPlayerProfile other = (CraftPlayerProfile)obj;
         if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
         } else if (!Objects.equals(this.name, other.name)) {
            return false;
         } else {
            this.rebuildDirtyProperties();
            other.rebuildDirtyProperties();
            return equals(this.properties, other.properties);
         }
      } else {
         return false;
      }
   }

   private static boolean equals(@Nonnull PropertyMap propertyMap, @Nonnull PropertyMap other) {
      if (propertyMap.size() != other.size()) {
         return false;
      } else {
         Iterator<Property> iterator1 = propertyMap.values().iterator();
         Iterator<Property> iterator2 = other.values().iterator();

         Property property1;
         Property property2;
         do {
            if (!iterator1.hasNext()) {
               return !iterator2.hasNext();
            }

            if (!iterator2.hasNext()) {
               return false;
            }

            property1 = (Property)iterator1.next();
            property2 = (Property)iterator2.next();
         } while(CraftProfileProperty.equals(property1, property2));

         return false;
      }
   }

   public int hashCode() {
      this.rebuildDirtyProperties();
      int result = 1;
      result = 31 * result + Objects.hashCode(this.uniqueId);
      result = 31 * result + Objects.hashCode(this.name);
      result = 31 * result + hashCode(this.properties);
      return result;
   }

   private static int hashCode(PropertyMap propertyMap) {
      int result = 1;

      Property property;
      for(Iterator var2 = propertyMap.values().iterator(); var2.hasNext(); result = 31 * result + CraftProfileProperty.hashCode(property)) {
         property = (Property)var2.next();
      }

      return result;
   }

   public CraftPlayerProfile clone() {
      return new CraftPlayerProfile(this);
   }

   public Map<String, Object> serialize() {
      Map<String, Object> map = new LinkedHashMap();
      if (this.uniqueId != null) {
         map.put("uniqueId", this.uniqueId.toString());
      }

      if (this.name != null) {
         map.put("name", this.name);
      }

      this.rebuildDirtyProperties();
      if (!this.properties.isEmpty()) {
         List<Object> propertiesData = new ArrayList();
         this.properties.forEach((propertyName, property) -> {
            propertiesData.add(CraftProfileProperty.serialize(property));
         });
         map.put("properties", propertiesData);
      }

      return map;
   }

   public static CraftPlayerProfile deserialize(Map<String, Object> map) {
      UUID uniqueId = ConfigSerializationUtil.getUuid(map, "uniqueId", true);
      String name = ConfigSerializationUtil.getString(map, "name", true);
      CraftPlayerProfile profile = new CraftPlayerProfile(uniqueId, name);
      if (map.containsKey("properties")) {
         Iterator var4 = ((List)map.get("properties")).iterator();

         while(var4.hasNext()) {
            Object propertyData = var4.next();
            Preconditions.checkArgument(propertyData instanceof Map, "Property data (%s) is not a valid Map", propertyData);
            Property property = CraftProfileProperty.deserialize((Map)propertyData);
            profile.properties.put(property.name(), property);
         }
      }

      return profile;
   }
}
