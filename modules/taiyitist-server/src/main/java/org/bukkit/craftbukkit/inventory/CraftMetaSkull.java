package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaSkull extends CraftMetaItem implements SkullMeta {
   static final CraftMetaItem.ItemMetaKeyType<ResolvableProfile> SKULL_PROFILE;
   static final ItemMetaKey SKULL_OWNER;
   static final ItemMetaKey BLOCK_ENTITY_TAG;
   static final CraftMetaItem.ItemMetaKeyType<ResourceLocation> NOTE_BLOCK_SOUND;
   static final int MAX_OWNER_LENGTH = 16;
   private ResolvableProfile profile;
   private ResourceLocation noteBlockSound;

   CraftMetaSkull(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaSkull skullMeta) {
         this.setProfile(skullMeta.profile);
         this.noteBlockSound = skullMeta.noteBlockSound;
      }
   }

   CraftMetaSkull(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, SKULL_PROFILE).ifPresent(this::setProfile);
      getOrEmpty(tag, NOTE_BLOCK_SOUND).ifPresent((minecraftKey) -> {
         this.noteBlockSound = minecraftKey;
      });
   }

   CraftMetaSkull(Map<String, Object> map) {
      super(map);
      Object object;
      if (this.profile == null) {
         object = map.get(SKULL_OWNER.BUKKIT);
         if (object instanceof PlayerProfile) {
            PlayerProfile playerProfile = (PlayerProfile)object;
            this.setOwnerProfile(playerProfile);
         } else {
            this.setOwner(SerializableMeta.getString(map, SKULL_OWNER.BUKKIT, true));
         }
      }

      if (this.noteBlockSound == null) {
         object = map.get(NOTE_BLOCK_SOUND.BUKKIT);
         if (object != null) {
            this.setNoteBlockSound(NamespacedKey.fromString(object.toString()));
         }
      }

   }

   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      tag.getCompound(SKULL_PROFILE.NBT).ifPresent((skullTag) -> {
         skullTag.getString("Id").ifPresent((id) -> {
            UUID uuid = UUID.fromString(id);
            skullTag.store("Id", UUIDUtil.CODEC, uuid);
         });
         ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, skullTag).result().ifPresent(this::setProfile);
      });
      tag.getCompound(BLOCK_ENTITY_TAG.NBT).flatMap((nbtTagCompound) -> {
         return nbtTagCompound.getString(NOTE_BLOCK_SOUND.NBT);
      }).ifPresent((noteBlockSound) -> {
         this.noteBlockSound = ResourceLocation.tryParse(noteBlockSound);
      });
   }

   private void setProfile(ResolvableProfile profile) {
      this.profile = profile;
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.hasOwner()) {
         tag.put(SKULL_PROFILE, this.profile);
         PlayerProfile ownerProfile = new CraftPlayerProfile(this.profile);
         if (ownerProfile.getTextures().isEmpty()) {
            ownerProfile.update().thenAccept((filledProfile) -> {
               this.setOwnerProfile(filledProfile);
               tag.put(SKULL_PROFILE, this.profile);
            });
         }
      }

      if (this.noteBlockSound != null) {
         tag.put(NOTE_BLOCK_SOUND, this.noteBlockSound);
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isSkullEmpty();
   }

   boolean isSkullEmpty() {
      return this.profile == null && this.noteBlockSound == null;
   }

   public CraftMetaSkull clone() {
      return (CraftMetaSkull)super.clone();
   }

   public boolean hasOwner() {
      return this.profile != null;
   }

   public String getOwner() {
      return this.hasOwner() ? (String)this.profile.name().orElse((String) null) : null;
   }

   public OfflinePlayer getOwningPlayer() {
      if (this.hasOwner()) {
         if (this.profile.id().filter((u) -> {
            return !u.equals(Util.NIL_UUID);
         }).isPresent()) {
            return Bukkit.getOfflinePlayer((UUID)this.profile.id().get());
         }

         if (this.profile.name().filter((s) -> {
            return !s.isEmpty();
         }).isPresent()) {
            return Bukkit.getOfflinePlayer((String)this.profile.name().get());
         }
      }

      return null;
   }

   public boolean setOwner(String name) {
      if (name != null && name.length() > 16) {
         return false;
      } else {
         if (name == null) {
            this.setProfile((ResolvableProfile)null);
         } else {
            this.setProfile(new ResolvableProfile(new GameProfile(Util.NIL_UUID, name)));
         }

         return true;
      }
   }

   public boolean setOwningPlayer(OfflinePlayer owner) {
      if (owner == null) {
         this.setProfile((ResolvableProfile)null);
      } else if (owner instanceof CraftPlayer) {
         CraftPlayer craftPlayer = (CraftPlayer)owner;
         this.setProfile(new ResolvableProfile(craftPlayer.getProfile()));
      } else {
         this.setProfile(new ResolvableProfile(new GameProfile(owner.getUniqueId(), owner.getName() == null ? "" : owner.getName())));
      }

      return true;
   }

   public PlayerProfile getOwnerProfile() {
      return !this.hasOwner() ? null : new CraftPlayerProfile(this.profile);
   }

   public void setOwnerProfile(PlayerProfile profile) {
      if (profile instanceof CraftPlayerProfile craftPlayerProfile) {
         this.setProfile(CraftPlayerProfile.validateSkullProfile(craftPlayerProfile.buildResolvableProfile()));
      } else {
         this.setProfile((ResolvableProfile)null);
      }

   }

   public void setNoteBlockSound(NamespacedKey noteBlockSound) {
      if (noteBlockSound == null) {
         this.noteBlockSound = null;
      } else {
         this.noteBlockSound = CraftNamespacedKey.toMinecraft(noteBlockSound);
      }

   }

   public NamespacedKey getNoteBlockSound() {
      return this.noteBlockSound == null ? null : CraftNamespacedKey.fromMinecraft(this.noteBlockSound);
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasOwner()) {
         hash = 61 * hash + this.profile.hashCode();
      }

      if (this.noteBlockSound != null) {
         hash = 61 * hash + this.noteBlockSound.hashCode();
      }

      return original != hash ? CraftMetaSkull.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaSkull)) {
         return true;
      } else {
         CraftMetaSkull that = (CraftMetaSkull)meta;
         return Objects.equals(this.profile, that.profile) && Objects.equals(this.noteBlockSound, that.noteBlockSound);
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaSkull || this.isSkullEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasOwner()) {
         builder.put(SKULL_OWNER.BUKKIT, new CraftPlayerProfile(this.profile));
      }

      NamespacedKey namespacedKeyNB = this.getNoteBlockSound();
      if (namespacedKeyNB != null) {
         builder.put(NOTE_BLOCK_SOUND.BUKKIT, namespacedKeyNB.toString());
      }

      return builder;
   }

   static {
      SKULL_PROFILE = new CraftMetaItem.ItemMetaKeyType(DataComponents.PROFILE, "SkullProfile");
      SKULL_OWNER = new ItemMetaKey("skull-owner");
      BLOCK_ENTITY_TAG = new ItemMetaKey("BlockEntityTag");
      NOTE_BLOCK_SOUND = new CraftMetaItem.ItemMetaKeyType(DataComponents.NOTE_BLOCK_SOUND, "note_block_sound");
   }
}
