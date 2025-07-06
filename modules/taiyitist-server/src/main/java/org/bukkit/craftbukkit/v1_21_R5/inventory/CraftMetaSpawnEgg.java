package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntitySnapshot;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SpawnEggMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaSpawnEgg extends CraftMetaItem implements SpawnEggMeta {
   static final CraftMetaItem.ItemMetaKeyType<CustomData> ENTITY_TAG;
   static final ItemMetaKey ENTITY_ID;
   private CompoundTag entityTag;

   CraftMetaSpawnEgg(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaSpawnEgg egg) {
         this.entityTag = egg.entityTag;
      }
   }

   CraftMetaSpawnEgg(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, ENTITY_TAG).ifPresent((nbt) -> {
         this.entityTag = nbt.copyTag();
      });
   }

   CraftMetaSpawnEgg(Map<String, Object> map) {
      super(map);
   }

   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      tag.getCompound(ENTITY_TAG.NBT).ifPresent((entityTag) -> {
         this.entityTag = entityTag;
         if (!entityTag.isEmpty()) {
         }

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

   boolean isEmpty() {
      return super.isEmpty() && this.isSpawnEggEmpty();
   }

   boolean isSpawnEggEmpty() {
      return this.entityTag == null;
   }

   public EntityType getSpawnedType() {
      throw new UnsupportedOperationException("Must check item type to get spawned type");
   }

   public void setSpawnedType(EntityType type) {
      throw new UnsupportedOperationException("Must change item type to set spawned type");
   }

   public EntitySnapshot getSpawnedEntity() {
      return CraftEntitySnapshot.create(this.entityTag);
   }

   public void setSpawnedEntity(EntitySnapshot snapshot) {
      Preconditions.checkArgument(snapshot.getEntityType().isSpawnable(), "Entity is not spawnable");
      this.entityTag = ((CraftEntitySnapshot)snapshot).getData();
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaSpawnEgg)) {
         return true;
      } else {
         CraftMetaSpawnEgg that = (CraftMetaSpawnEgg)meta;
         return this.entityTag != null ? that.entityTag != null && this.entityTag.equals(that.entityTag) : this.entityTag == null;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaSpawnEgg || this.isSpawnEggEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.entityTag != null) {
         hash = 73 * hash + this.entityTag.hashCode();
      }

      return original != hash ? CraftMetaSpawnEgg.class.hashCode() ^ hash : hash;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      return builder;
   }

   public CraftMetaSpawnEgg clone() {
      CraftMetaSpawnEgg clone = (CraftMetaSpawnEgg)super.clone();
      if (this.entityTag != null) {
         clone.entityTag = this.entityTag.copy();
      }

      return clone;
   }

   static {
      ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType(DataComponents.ENTITY_DATA, "entity-tag");
      ENTITY_ID = new ItemMetaKey("id");
   }
}
