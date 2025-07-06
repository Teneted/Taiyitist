package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.FireworkEffectMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaCharge extends CraftMetaItem implements FireworkEffectMeta {
   static final CraftMetaItem.ItemMetaKeyType<FireworkExplosion> EXPLOSION;
   private FireworkEffect effect;

   CraftMetaCharge(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaCharge) {
         this.effect = ((CraftMetaCharge)meta).effect;
      }

   }

   CraftMetaCharge(Map<String, Object> map) {
      super(map);
      this.setEffect((FireworkEffect)SerializableMeta.getObject(FireworkEffect.class, map, EXPLOSION.BUKKIT, true));
   }

   CraftMetaCharge(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, EXPLOSION).ifPresent((f) -> {
         try {
            this.effect = CraftMetaFirework.getEffect(f);
         } catch (IllegalArgumentException var3) {
         }

      });
   }

   public void setEffect(FireworkEffect effect) {
      this.effect = effect;
   }

   public boolean hasEffect() {
      return this.effect != null;
   }

   public FireworkEffect getEffect() {
      return this.effect;
   }

   void applyToItem(CraftMetaItem.Applicator itemTag) {
      super.applyToItem(itemTag);
      if (this.hasEffect()) {
         itemTag.put(EXPLOSION, CraftMetaFirework.getExplosion(this.effect));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && !this.hasChargeMeta();
   }

   boolean hasChargeMeta() {
      return this.hasEffect();
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaCharge)) {
         return true;
      } else {
         CraftMetaCharge that = (CraftMetaCharge)meta;
         return this.hasEffect() ? that.hasEffect() && this.effect.equals(that.effect) : !that.hasEffect();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaCharge || !this.hasChargeMeta());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasEffect()) {
         hash = 61 * hash + this.effect.hashCode();
      }

      return hash != original ? CraftMetaCharge.class.hashCode() ^ hash : hash;
   }

   public CraftMetaCharge clone() {
      return (CraftMetaCharge)super.clone();
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasEffect()) {
         builder.put(EXPLOSION.BUKKIT, this.effect);
      }

      return builder;
   }

   static {
      EXPLOSION = new CraftMetaItem.ItemMetaKeyType(DataComponents.FIREWORK_EXPLOSION, "firework-effect");
   }
}
