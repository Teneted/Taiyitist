package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.DyeColor;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftTropicalFish;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaTropicalFishBucket extends CraftMetaItem implements TropicalFishBucketMeta {
   static final ItemMetaKey VARIANT = new ItemMetaKey("BucketVariantTag", "fish-variant");
   static final CraftMetaItem.ItemMetaKeyType<CustomData> ENTITY_TAG;
   static final CraftMetaItem.ItemMetaKeyType<CustomData> BUCKET_ENTITY_TAG;
   private Integer variant;
   private CompoundTag entityTag;
   private CompoundTag bucketEntityTag;

   CraftMetaTropicalFishBucket(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaTropicalFishBucket bucket) {
         this.variant = bucket.variant;
         this.entityTag = bucket.entityTag;
         this.bucketEntityTag = bucket.bucketEntityTag;
      }
   }

   CraftMetaTropicalFishBucket(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, ENTITY_TAG).ifPresent((nbt) -> {
         this.entityTag = nbt.copyTag();
         this.entityTag.getInt(VARIANT.NBT).ifPresent((variant) -> {
            this.variant = variant;
         });
      });
      getOrEmpty(tag, BUCKET_ENTITY_TAG).ifPresent((nbt) -> {
         this.bucketEntityTag = nbt.copyTag();
         this.bucketEntityTag.getInt(VARIANT.NBT).ifPresent((variant) -> {
            this.variant = variant;
         });
      });
   }

   CraftMetaTropicalFishBucket(Map<String, Object> map) {
      super(map);
      Integer variant = (Integer)SerializableMeta.getObject(Integer.class, map, VARIANT.BUKKIT, true);
      if (variant != null) {
         this.variant = variant;
      }

   }

   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      this.entityTag = (CompoundTag)tag.getCompound(ENTITY_TAG.NBT).orElse(this.entityTag);
      this.bucketEntityTag = (CompoundTag)tag.getCompound(BUCKET_ENTITY_TAG.NBT).orElse(this.bucketEntityTag);
   }

   void serializeInternal(Map<String, Tag> internalTags) {
      if (this.entityTag != null && !this.entityTag.isEmpty()) {
         internalTags.put(ENTITY_TAG.NBT, this.entityTag);
      }

      if (this.bucketEntityTag != null && !this.bucketEntityTag.isEmpty()) {
         internalTags.put(BUCKET_ENTITY_TAG.NBT, this.bucketEntityTag);
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.entityTag != null) {
         tag.put(ENTITY_TAG, CustomData.of(this.entityTag));
      }

      CompoundTag bucketEntityTag = this.bucketEntityTag != null ? this.bucketEntityTag.copy() : null;
      if (this.hasVariant()) {
         if (bucketEntityTag == null) {
            bucketEntityTag = new CompoundTag();
         }

         bucketEntityTag.putInt(VARIANT.NBT, this.variant);
      }

      if (bucketEntityTag != null) {
         tag.put(BUCKET_ENTITY_TAG, CustomData.of(bucketEntityTag));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBucketEmpty();
   }

   boolean isBucketEmpty() {
      return !this.hasVariant() && this.entityTag == null && this.bucketEntityTag == null;
   }

   public DyeColor getPatternColor() {
      return CraftTropicalFish.getPatternColor(this.variant);
   }

   public void setPatternColor(DyeColor color) {
      if (this.variant == null) {
         this.variant = 0;
      }

      this.variant = CraftTropicalFish.getData(color, this.getPatternColor(), this.getPattern());
   }

   public DyeColor getBodyColor() {
      return CraftTropicalFish.getBodyColor(this.variant);
   }

   public void setBodyColor(DyeColor color) {
      if (this.variant == null) {
         this.variant = 0;
      }

      this.variant = CraftTropicalFish.getData(this.getPatternColor(), color, this.getPattern());
   }

   public TropicalFish.Pattern getPattern() {
      return CraftTropicalFish.getPattern(this.variant);
   }

   public void setPattern(TropicalFish.Pattern pattern) {
      if (this.variant == null) {
         this.variant = 0;
      }

      this.variant = CraftTropicalFish.getData(this.getPatternColor(), this.getBodyColor(), pattern);
   }

   public boolean hasVariant() {
      return this.variant != null;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaTropicalFishBucket)) {
         return true;
      } else {
         boolean var10000;
         label60: {
            label53: {
               CraftMetaTropicalFishBucket that = (CraftMetaTropicalFishBucket)meta;
               if (this.hasVariant()) {
                  if (!that.hasVariant() || !this.variant.equals(that.variant)) {
                     break label53;
                  }
               } else if (that.hasVariant()) {
                  break label53;
               }

               if (this.entityTag != null) {
                  if (that.entityTag == null || !this.entityTag.equals(that.entityTag)) {
                     break label53;
                  }
               } else if (that.entityTag != null) {
                  break label53;
               }

               if (this.bucketEntityTag != null) {
                  if (that.bucketEntityTag != null && this.bucketEntityTag.equals(that.bucketEntityTag)) {
                     break label60;
                  }
               } else if (that.bucketEntityTag == null) {
                  break label60;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaTropicalFishBucket || this.isBucketEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasVariant()) {
         hash = 61 * hash + this.variant;
      }

      if (this.entityTag != null) {
         hash = 61 * hash + this.entityTag.hashCode();
      }

      if (this.bucketEntityTag != null) {
         hash = 61 * hash + this.bucketEntityTag.hashCode();
      }

      return original != hash ? CraftMetaTropicalFishBucket.class.hashCode() ^ hash : hash;
   }

   public CraftMetaTropicalFishBucket clone() {
      CraftMetaTropicalFishBucket clone = (CraftMetaTropicalFishBucket)super.clone();
      if (this.entityTag != null) {
         clone.entityTag = this.entityTag.copy();
      }

      if (this.bucketEntityTag != null) {
         clone.bucketEntityTag = this.bucketEntityTag.copy();
      }

      return clone;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasVariant()) {
         builder.put(VARIANT.BUKKIT, this.variant);
      }

      return builder;
   }

   static {
      ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType(DataComponents.ENTITY_DATA, "entity-tag");
      BUCKET_ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType(DataComponents.BUCKET_ENTITY_DATA, "bucket-entity-tag");
   }
}
