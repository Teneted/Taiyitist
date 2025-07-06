package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_21_R5.potion.CraftPotionEffectType;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaSuspiciousStew extends CraftMetaItem implements SuspiciousStewMeta {
   static final CraftMetaItem.ItemMetaKeyType<SuspiciousStewEffects> EFFECTS;
   private List<PotionEffect> customEffects;

   CraftMetaSuspiciousStew(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaSuspiciousStew stewMeta) {
         if (stewMeta.hasCustomEffects()) {
            this.customEffects = new ArrayList(stewMeta.customEffects);
         }

      }
   }

   CraftMetaSuspiciousStew(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, EFFECTS).ifPresent((suspiciousStewEffects) -> {
         List<SuspiciousStewEffects.Entry> list = suspiciousStewEffects.effects();
         int length = list.size();
         this.customEffects = new ArrayList(length);

         for(int i = 0; i < length; ++i) {
            SuspiciousStewEffects.Entry effect = (SuspiciousStewEffects.Entry)list.get(i);
            PotionEffectType type = CraftPotionEffectType.minecraftHolderToBukkit(effect.effect());
            if (type != null) {
               int duration = effect.duration();
               this.customEffects.add(new PotionEffect(type, duration, 0));
            }
         }

      });
   }

   CraftMetaSuspiciousStew(Map<String, Object> map) {
      super(map);
      Iterable<?> rawEffectList = (Iterable)SerializableMeta.getObject(Iterable.class, map, EFFECTS.BUKKIT, true);
      if (rawEffectList != null) {
         Iterator var3 = rawEffectList.iterator();

         while(var3.hasNext()) {
            Object obj = var3.next();
            Preconditions.checkArgument(obj instanceof PotionEffect, "Object (%s) in effect list is not valid", obj.getClass());
            this.addCustomEffect((PotionEffect)obj, true);
         }

      }
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.customEffects != null) {
         List<SuspiciousStewEffects.Entry> effectList = new ArrayList();
         Iterator var3 = this.customEffects.iterator();

         while(var3.hasNext()) {
            PotionEffect effect = (PotionEffect)var3.next();
            effectList.add(new SuspiciousStewEffects.Entry(CraftPotionEffectType.bukkitToMinecraftHolder(effect.getType()), effect.getDuration()));
         }

         tag.put(EFFECTS, new SuspiciousStewEffects(effectList));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isStewEmpty();
   }

   boolean isStewEmpty() {
      return !this.hasCustomEffects();
   }

   public CraftMetaSuspiciousStew clone() {
      CraftMetaSuspiciousStew clone = (CraftMetaSuspiciousStew)super.clone();
      if (this.customEffects != null) {
         clone.customEffects = new ArrayList(this.customEffects);
      }

      return clone;
   }

   public boolean hasCustomEffects() {
      return this.customEffects != null;
   }

   public List<PotionEffect> getCustomEffects() {
      return this.hasCustomEffects() ? ImmutableList.copyOf(this.customEffects) : ImmutableList.of();
   }

   public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
      Preconditions.checkArgument(effect != null, "Potion effect cannot be null");
      int index = this.indexOfEffect(effect.getType());
      if (index != -1) {
         if (overwrite) {
            PotionEffect old = (PotionEffect)this.customEffects.get(index);
            if (old.getDuration() == effect.getDuration()) {
               return false;
            } else {
               this.customEffects.set(index, effect);
               return true;
            }
         } else {
            return false;
         }
      } else {
         if (this.customEffects == null) {
            this.customEffects = new ArrayList();
         }

         this.customEffects.add(effect);
         return true;
      }
   }

   public boolean removeCustomEffect(PotionEffectType type) {
      Preconditions.checkArgument(type != null, "Potion effect type cannot be null");
      if (!this.hasCustomEffects()) {
         return false;
      } else {
         boolean changed = false;
         Iterator<PotionEffect> iterator = this.customEffects.iterator();

         while(iterator.hasNext()) {
            PotionEffect effect = (PotionEffect)iterator.next();
            if (type.equals(effect.getType())) {
               iterator.remove();
               changed = true;
            }
         }

         if (this.customEffects.isEmpty()) {
            this.customEffects = null;
         }

         return changed;
      }
   }

   public boolean hasCustomEffect(PotionEffectType type) {
      Preconditions.checkArgument(type != null, "Potion effect type cannot be null");
      return this.indexOfEffect(type) != -1;
   }

   private int indexOfEffect(PotionEffectType type) {
      if (!this.hasCustomEffects()) {
         return -1;
      } else {
         for(int i = 0; i < this.customEffects.size(); ++i) {
            if (((PotionEffect)this.customEffects.get(i)).getType().equals(type)) {
               return i;
            }
         }

         return -1;
      }
   }

   public boolean clearCustomEffects() {
      boolean changed = this.hasCustomEffects();
      this.customEffects = null;
      return changed;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasCustomEffects()) {
         hash = 73 * hash + this.customEffects.hashCode();
      }

      return original != hash ? CraftMetaSuspiciousStew.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaSuspiciousStew)) {
         return true;
      } else {
         CraftMetaSuspiciousStew that = (CraftMetaSuspiciousStew)meta;
         return this.hasCustomEffects() ? that.hasCustomEffects() && this.customEffects.equals(that.customEffects) : !that.hasCustomEffects();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaSuspiciousStew || this.isStewEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasCustomEffects()) {
         builder.put(EFFECTS.BUKKIT, ImmutableList.copyOf(this.customEffects));
      }

      return builder;
   }

   static {
      EFFECTS = new CraftMetaItem.ItemMetaKeyType(DataComponents.SUSPICIOUS_STEW_EFFECTS, "effects");
   }
}
