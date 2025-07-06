package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaArmorStand extends CraftMetaItem {
   static final CraftMetaItem.ItemMetaKeyType<CustomData> ENTITY_TAG;
   CompoundTag entityTag;

   CraftMetaArmorStand(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaArmorStand armorStand) {
         this.entityTag = armorStand.entityTag;
      }
   }

   CraftMetaArmorStand(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, ENTITY_TAG).ifPresent((nbt) -> {
         this.entityTag = nbt.copyTag();
      });
   }

   CraftMetaArmorStand(Map<String, Object> map) {
      super(map);
   }

   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      this.entityTag = (CompoundTag)tag.getCompound(ENTITY_TAG.NBT).orElse(this.entityTag);
   }

   void serializeInternal(Map<String, Tag> internalTags) {
      if (this.entityTag != null && !this.entityTag.isEmpty()) {
         internalTags.put(ENTITY_TAG.NBT, this.entityTag);
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.entityTag != null) {
         tag.put(ENTITY_TAG, CustomData.of(this.entityTag));
      }

   }

   boolean applicableTo(Material type) {
      return type == Material.ARMOR_STAND;
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isArmorStandEmpty();
   }

   boolean isArmorStandEmpty() {
      return this.entityTag == null;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaArmorStand)) {
         return true;
      } else {
         CraftMetaArmorStand that = (CraftMetaArmorStand)meta;
         return this.entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : this.entityTag == null;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaArmorStand || this.isArmorStandEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.entityTag != null) {
         hash = 73 * hash + this.entityTag.hashCode();
      }

      return original != hash ? CraftMetaArmorStand.class.hashCode() ^ hash : hash;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      return builder;
   }

   public CraftMetaArmorStand clone() {
      CraftMetaArmorStand clone = (CraftMetaArmorStand)super.clone();
      if (this.entityTag != null) {
         clone.entityTag = this.entityTag.copy();
      }

      return clone;
   }

   static {
      ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType(DataComponents.ENTITY_DATA, "entity-tag");
   }
}
