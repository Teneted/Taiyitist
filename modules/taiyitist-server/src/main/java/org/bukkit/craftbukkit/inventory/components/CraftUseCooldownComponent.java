package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.UseCooldown;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.components.UseCooldownComponent;

@SerializableAs("UseCooldown")
public final class CraftUseCooldownComponent implements UseCooldownComponent {
   private UseCooldown handle;

   public CraftUseCooldownComponent(UseCooldown cooldown) {
      this.handle = cooldown;
   }

   public CraftUseCooldownComponent(CraftUseCooldownComponent food) {
      this.handle = food.handle;
   }

   public CraftUseCooldownComponent(Map<String, Object> map) {
      Float seconds = (Float)SerializableMeta.getObject(Float.class, map, "seconds", false);
      String cooldownGroup = SerializableMeta.getString(map, "cooldown-group", true);
      this.handle = new UseCooldown(seconds, Optional.ofNullable(cooldownGroup).map(ResourceLocation::parse));
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("seconds", this.getCooldownSeconds());
      if (this.getCooldownGroup() != null) {
         result.put("cooldown-group", this.getCooldownGroup().toString());
      }

      return result;
   }

   public UseCooldown getHandle() {
      return this.handle;
   }

   public float getCooldownSeconds() {
      return this.handle.seconds();
   }

   public void setCooldownSeconds(float cooldown) {
      Preconditions.checkArgument(cooldown > 0.0F, "cooldown must be greater than 0");
      this.handle = new UseCooldown(cooldown, this.handle.cooldownGroup());
   }

   public NamespacedKey getCooldownGroup() {
      return (NamespacedKey)this.handle.cooldownGroup().map(CraftNamespacedKey::fromMinecraft).orElse((Object)null);
   }

   public void setCooldownGroup(NamespacedKey song) {
      this.handle = new UseCooldown(this.handle.seconds(), Optional.ofNullable(song).map(CraftNamespacedKey::toMinecraft));
   }

   public int hashCode() {
      int hash = 7;
      hash = 73 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftUseCooldownComponent other = (CraftUseCooldownComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftUseCooldownComponent{handle=" + String.valueOf(this.handle) + "}";
   }
}
