package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaEntityTag extends CraftMetaItem {
   private static final Set<Material> ENTITY_TAGGABLE_MATERIALS;
   static final CraftMetaItem.ItemMetaKeyType<CustomData> ENTITY_TAG;
   CompoundTag entityTag;

   CraftMetaEntityTag(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaEntityTag entity) {
         this.entityTag = entity.entityTag;
      }
   }

   CraftMetaEntityTag(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, ENTITY_TAG).ifPresent((nbt) -> {
         this.entityTag = nbt.copyTag();
      });
   }

   CraftMetaEntityTag(Map<String, Object> map) {
      super(map);
   }

   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      tag.getCompound(ENTITY_TAG.NBT).ifPresent((entityTag) -> {
         this.entityTag = entityTag;
      });
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
      return ENTITY_TAGGABLE_MATERIALS.contains(type);
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isEntityTagEmpty();
   }

   boolean isEntityTagEmpty() {
      return this.entityTag == null;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaEntityTag)) {
         return true;
      } else {
         CraftMetaEntityTag that = (CraftMetaEntityTag)meta;
         return this.entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : this.entityTag == null;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaEntityTag || this.isEntityTagEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.entityTag != null) {
         hash = 73 * hash + this.entityTag.hashCode();
      }

      return original != hash ? CraftMetaEntityTag.class.hashCode() ^ hash : hash;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      return builder;
   }

   public CraftMetaEntityTag clone() {
      CraftMetaEntityTag clone = (CraftMetaEntityTag)super.clone();
      if (this.entityTag != null) {
         clone.entityTag = this.entityTag.copy();
      }

      return clone;
   }

   static {
      ENTITY_TAGGABLE_MATERIALS = Sets.newHashSet(new Material[]{Material.COD_BUCKET, Material.PUFFERFISH_BUCKET, Material.SALMON_BUCKET, Material.ITEM_FRAME, Material.GLOW_ITEM_FRAME, Material.PAINTING});
      ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType(DataComponents.ENTITY_DATA, "EntityTag", "entity-tag");
   }
}
