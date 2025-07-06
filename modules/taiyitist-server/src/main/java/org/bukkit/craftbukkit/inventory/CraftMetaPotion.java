package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaPotion extends CraftMetaItem implements PotionMeta {
   static final CraftMetaItem.ItemMetaKeyType<PotionContents> POTION_CONTENTS;
   static final CraftMetaItem.ItemMetaKeyType<Float> POTION_DURATION_SCALE;
   static final ItemMetaKey POTION_EFFECTS;
   static final ItemMetaKey POTION_COLOR;
   static final ItemMetaKey CUSTOM_NAME;
   static final ItemMetaKey DEFAULT_POTION;
   private PotionType type;
   private List<PotionEffect> customEffects;
   private Color color;
   private String customName;
   private Float potionDurationScale;

   CraftMetaPotion(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaPotion potionMeta) {
         this.type = potionMeta.type;
         this.color = potionMeta.color;
         this.customName = potionMeta.customName;
         if (potionMeta.hasCustomEffects()) {
            this.customEffects = new ArrayList(potionMeta.customEffects);
         }

         this.potionDurationScale = potionMeta.potionDurationScale;
      }
   }

   CraftMetaPotion(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, POTION_CONTENTS).ifPresent((potionContents) -> {
         potionContents.potion().ifPresent((potion) -> {
            this.type = CraftPotionType.minecraftHolderToBukkit(potion);
         });
         potionContents.customColor().ifPresent((customColor) -> {
            try {
               this.color = Color.fromRGB(customColor);
            } catch (IllegalArgumentException var3) {
            }

         });
         potionContents.customName().ifPresent((name) -> {
            this.customName = name;
         });
         List<MobEffectInstance> list = potionContents.customEffects();
         int length = list.size();
         this.customEffects = new ArrayList(length);

         for(int i = 0; i < length; ++i) {
            MobEffectInstance effect = (MobEffectInstance)list.get(i);
            PotionEffectType type = CraftPotionEffectType.minecraftHolderToBukkit(effect.getEffect());
            if (type != null) {
               int amp = effect.getAmplifier();
               int duration = effect.getDuration();
               boolean ambient = effect.isAmbient();
               boolean particles = effect.isVisible();
               boolean icon = effect.showIcon();
               this.customEffects.add(new PotionEffect(type, duration, amp, ambient, particles, icon));
            }
         }

      });
      getOrEmpty(tag, POTION_DURATION_SCALE).ifPresent((potionDurationScale) -> {
         this.potionDurationScale = potionDurationScale;
      });
   }

   CraftMetaPotion(Map<String, Object> map) {
      super(map);
      String typeString = SerializableMeta.getString(map, DEFAULT_POTION.BUKKIT, true);
      if (typeString != null) {
         this.type = CraftPotionType.stringToBukkit(typeString);
      }

      Color color = (Color)SerializableMeta.getObject(Color.class, map, POTION_COLOR.BUKKIT, true);
      if (color != null) {
         this.setColor(color);
      }

      String name = SerializableMeta.getString(map, CUSTOM_NAME.BUKKIT, true);
      if (name != null) {
         this.setCustomName(name);
      }

      Iterable<?> rawEffectList = (Iterable)SerializableMeta.getObject(Iterable.class, map, POTION_EFFECTS.BUKKIT, true);
      if (rawEffectList != null) {
         Iterator var6 = rawEffectList.iterator();

         while(var6.hasNext()) {
            Object obj = var6.next();
            Preconditions.checkArgument(obj instanceof PotionEffect, "Object (%s) in effect list is not valid", obj.getClass());
            this.addCustomEffect((PotionEffect)obj, true);
         }

         Float scale = (Float)SerializableMeta.getObject(Float.class, map, POTION_DURATION_SCALE.BUKKIT, true);
         if (scale != null) {
            this.setDurationScale(scale);
         }

      }
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (!this.isPotionEmpty()) {
         Optional<Holder<Potion>> defaultPotion = this.hasBasePotionType() ? Optional.of(CraftPotionType.bukkitToMinecraftHolder(this.type)) : Optional.empty();
         Optional<Integer> potionColor = this.hasColor() ? Optional.of(this.color.asRGB()) : Optional.empty();
         Optional<String> customName = Optional.ofNullable(this.customName);
         List<MobEffectInstance> effectList = new ArrayList();
         if (this.customEffects != null) {
            Iterator var6 = this.customEffects.iterator();

            while(var6.hasNext()) {
               PotionEffect effect = (PotionEffect)var6.next();
               effectList.add(new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraftHolder(effect.getType()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
            }
         }

         tag.put(POTION_CONTENTS, new PotionContents(defaultPotion, potionColor, effectList, customName));
         if (this.hasDurationScale()) {
            tag.put(POTION_DURATION_SCALE, this.getDurationScale());
         }

      }
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isPotionEmpty();
   }

   boolean isPotionEmpty() {
      return this.type == null && !this.hasCustomEffects() && !this.hasColor() && !this.hasCustomName() && !this.hasDurationScale();
   }

   public CraftMetaPotion clone() {
      CraftMetaPotion clone = (CraftMetaPotion)super.clone();
      clone.type = this.type;
      if (this.customEffects != null) {
         clone.customEffects = new ArrayList(this.customEffects);
      }

      return clone;
   }

   public void setBasePotionData(PotionData data) {
      this.setBasePotionType(CraftPotionUtil.fromBukkit(data));
   }

   public PotionData getBasePotionData() {
      return CraftPotionUtil.toBukkit(this.getBasePotionType());
   }

   public void setBasePotionType(PotionType potionType) {
      this.type = potionType;
   }

   public PotionType getBasePotionType() {
      return this.type;
   }

   public boolean hasBasePotionType() {
      return this.type != null;
   }

   public boolean hasCustomEffects() {
      return this.customEffects != null && !this.customEffects.isEmpty();
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
            if (old.getAmplifier() == effect.getAmplifier() && old.getDuration() == effect.getDuration() && old.isAmbient() == effect.isAmbient()) {
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

   public boolean setMainEffect(PotionEffectType type) {
      Preconditions.checkArgument(type != null, "Potion effect type cannot be null");
      int index = this.indexOfEffect(type);
      if (index != -1 && index != 0) {
         PotionEffect old = (PotionEffect)this.customEffects.get(0);
         this.customEffects.set(0, (PotionEffect)this.customEffects.get(index));
         this.customEffects.set(index, old);
         return true;
      } else {
         return false;
      }
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

   public boolean hasColor() {
      return this.color != null;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public String getCustomName() {
      return this.customName;
   }

   public void setCustomName(String customName) {
      this.customName = customName;
   }

   public boolean hasDurationScale() {
      return this.potionDurationScale != null;
   }

   public float getDurationScale() {
      Preconditions.checkState(this.hasDurationScale(), "hasDurationScale is false");
      return this.potionDurationScale;
   }

   public void setDurationScale(Float scale) {
      this.potionDurationScale = scale;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.type != null) {
         hash = 73 * hash + this.type.hashCode();
      }

      if (this.hasColor()) {
         hash = 73 * hash + this.color.hashCode();
      }

      if (this.hasCustomName()) {
         hash = 73 * hash + this.customName.hashCode();
      }

      if (this.hasCustomEffects()) {
         hash = 73 * hash + this.customEffects.hashCode();
      }

      if (this.hasDurationScale()) {
         hash = 73 * hash + this.potionDurationScale.hashCode();
      }

      return original != hash ? CraftMetaPotion.class.hashCode() ^ hash : hash;
   }

   public boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaPotion)) {
         return true;
      } else {
         boolean var10000;
         label76: {
            CraftMetaPotion that = (CraftMetaPotion)meta;
            if (Objects.equals(this.type, that.type)) {
               label70: {
                  if (this.hasCustomEffects()) {
                     if (!that.hasCustomEffects() || !this.customEffects.equals(that.customEffects)) {
                        break label70;
                     }
                  } else if (that.hasCustomEffects()) {
                     break label70;
                  }

                  if (this.hasColor()) {
                     if (!that.hasColor() || !this.color.equals(that.color)) {
                        break label70;
                     }
                  } else if (that.hasColor()) {
                     break label70;
                  }

                  if (this.hasCustomName()) {
                     if (!that.hasCustomName() || !this.customName.equals(that.customName)) {
                        break label70;
                     }
                  } else if (that.hasCustomName()) {
                     break label70;
                  }

                  if (this.hasDurationScale()) {
                     if (that.hasDurationScale() && this.potionDurationScale.equals(that.potionDurationScale)) {
                        break label76;
                     }
                  } else if (!that.hasDurationScale()) {
                     break label76;
                  }
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
      return super.notUncommon(meta) && (meta instanceof CraftMetaPotion || this.isPotionEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.type != null) {
         builder.put(DEFAULT_POTION.BUKKIT, CraftPotionType.bukkitToString(this.type));
      }

      if (this.hasColor()) {
         builder.put(POTION_COLOR.BUKKIT, this.getColor());
      }

      if (this.hasCustomName()) {
         builder.put(CUSTOM_NAME.BUKKIT, this.getCustomName());
      }

      if (this.hasCustomEffects()) {
         builder.put(POTION_EFFECTS.BUKKIT, ImmutableList.copyOf(this.customEffects));
      }

      if (this.hasDurationScale()) {
         builder.put(POTION_DURATION_SCALE.BUKKIT, this.getDurationScale());
      }

      return builder;
   }

   static {
      POTION_CONTENTS = new CraftMetaItem.ItemMetaKeyType(DataComponents.POTION_CONTENTS);
      POTION_DURATION_SCALE = new CraftMetaItem.ItemMetaKeyType(DataComponents.POTION_DURATION_SCALE, "potion-duration-scale");
      POTION_EFFECTS = new ItemMetaKey("custom-effects");
      POTION_COLOR = new ItemMetaKey("custom-color");
      CUSTOM_NAME = new ItemMetaKey("custom-name");
      DEFAULT_POTION = new ItemMetaKey("potion-type");
   }
}
