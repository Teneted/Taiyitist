package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.OminousBottleMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaOminousBottle extends CraftMetaItem implements OminousBottleMeta {
   static final CraftMetaItem.ItemMetaKeyType<OminousBottleAmplifier> OMINOUS_BOTTLE_AMPLIFIER;
   private Integer ominousBottleAmplifier;

   CraftMetaOminousBottle(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaOminousBottle bottleMeta) {
         this.ominousBottleAmplifier = bottleMeta.ominousBottleAmplifier;
      }
   }

   CraftMetaOminousBottle(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, OMINOUS_BOTTLE_AMPLIFIER).ifPresent((amplifier) -> {
         this.ominousBottleAmplifier = amplifier.value();
      });
   }

   CraftMetaOminousBottle(Map<String, Object> map) {
      super(map);
      Integer ominousBottleAmplifier = (Integer)SerializableMeta.getObject(Integer.class, map, OMINOUS_BOTTLE_AMPLIFIER.BUKKIT, true);
      if (ominousBottleAmplifier != null) {
         this.setAmplifier(ominousBottleAmplifier);
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.hasAmplifier()) {
         tag.put(OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(this.ominousBottleAmplifier));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBottleEmpty();
   }

   boolean isBottleEmpty() {
      return !this.hasAmplifier();
   }

   public CraftMetaOminousBottle clone() {
      CraftMetaOminousBottle clone = (CraftMetaOminousBottle)super.clone();
      return clone;
   }

   public boolean hasAmplifier() {
      return this.ominousBottleAmplifier != null;
   }

   public int getAmplifier() {
      return this.ominousBottleAmplifier;
   }

   public void setAmplifier(int amplifier) {
      Preconditions.checkArgument(0 <= amplifier && amplifier <= 4, "Amplifier must be in range [0, 4]");
      this.ominousBottleAmplifier = amplifier;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasAmplifier()) {
         hash = 61 * hash + this.ominousBottleAmplifier.hashCode();
      }

      return original != hash ? CraftMetaOminousBottle.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaOminousBottle)) {
         return true;
      } else {
         CraftMetaOminousBottle that = (CraftMetaOminousBottle)meta;
         return this.hasAmplifier() ? that.hasAmplifier() && this.ominousBottleAmplifier.equals(that.ominousBottleAmplifier) : !that.hasAmplifier();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaOminousBottle || this.isBottleEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasAmplifier()) {
         builder.put(OMINOUS_BOTTLE_AMPLIFIER.BUKKIT, this.ominousBottleAmplifier);
      }

      return builder;
   }

   static {
      OMINOUS_BOTTLE_AMPLIFIER = new CraftMetaItem.ItemMetaKeyType(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, "ominous-bottle-amplifier");
   }
}
