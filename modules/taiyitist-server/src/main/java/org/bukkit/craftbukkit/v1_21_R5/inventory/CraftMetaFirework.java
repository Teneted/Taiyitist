package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.FireworkMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaFirework extends CraftMetaItem implements FireworkMeta {
   static final CraftMetaItem.ItemMetaKeyType<Fireworks> FIREWORKS;
   static final ItemMetaKey FLIGHT;
   static final ItemMetaKey EXPLOSIONS;
   private List<FireworkEffect> effects;
   private Integer power;

   CraftMetaFirework(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaFirework that) {
         this.power = that.power;
         if (that.hasEffects()) {
            this.effects = new ArrayList(that.effects);
         }

      }
   }

   CraftMetaFirework(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, FIREWORKS).ifPresent((fireworks) -> {
         this.power = fireworks.flightDuration();
         List<FireworkExplosion> fireworkEffects = fireworks.explosions();
         List<FireworkEffect> effects = this.effects = new ArrayList(fireworkEffects.size());

         for(int i = 0; i < fireworkEffects.size(); ++i) {
            try {
               effects.add(getEffect((FireworkExplosion)fireworkEffects.get(i)));
            } catch (IllegalArgumentException var6) {
            }
         }

      });
   }

   CraftMetaFirework(Map<String, Object> map) {
      super(map);
      Integer power = (Integer)SerializableMeta.getObject(Integer.class, map, FLIGHT.BUKKIT, true);
      if (power != null) {
         this.power = power;
      }

      Iterable<?> effects = (Iterable)SerializableMeta.getObject(Iterable.class, map, EXPLOSIONS.BUKKIT, true);
      this.safelyAddEffects(effects);
   }

   static FireworkEffect getEffect(FireworkExplosion explosion) {
      FireworkEffect.Builder effect = FireworkEffect.builder().flicker(explosion.hasTwinkle()).trail(explosion.hasTrail()).with(getEffectType(explosion.shape()));
      IntList colors = explosion.colors();
      if (colors.isEmpty()) {
         effect.withColor(Color.WHITE);
      }

      IntListIterator var3 = colors.iterator();

      int color;
      while(var3.hasNext()) {
         color = (Integer)var3.next();
         effect.withColor(Color.fromRGB(color));
      }

      var3 = explosion.fadeColors().iterator();

      while(var3.hasNext()) {
         color = (Integer)var3.next();
         effect.withFade(Color.fromRGB(color));
      }

      return effect.build();
   }

   static FireworkExplosion getExplosion(FireworkEffect effect) {
      IntList colors = addColors(effect.getColors());
      IntList fadeColors = addColors(effect.getFadeColors());
      return new FireworkExplosion(getNBT(effect.getType()), colors, fadeColors, effect.hasTrail(), effect.hasFlicker());
   }

   static FireworkExplosion.Shape getNBT(FireworkEffect.Type type) {
      switch (type) {
         case BALL -> {
            return Shape.SMALL_BALL;
         }
         case BALL_LARGE -> {
            return Shape.LARGE_BALL;
         }
         case STAR -> {
            return Shape.STAR;
         }
         case CREEPER -> {
            return Shape.CREEPER;
         }
         case BURST -> {
            return Shape.BURST;
         }
         default -> throw new IllegalArgumentException("Unknown effect type " + String.valueOf(type));
      }
   }

   static FireworkEffect.Type getEffectType(FireworkExplosion.Shape nbt) {
      switch (nbt) {
         case SMALL_BALL -> {
            return Type.BALL;
         }
         case LARGE_BALL -> {
            return Type.BALL_LARGE;
         }
         case STAR -> {
            return Type.STAR;
         }
         case CREEPER -> {
            return Type.CREEPER;
         }
         case BURST -> {
            return Type.BURST;
         }
         default -> throw new IllegalArgumentException("Unknown effect type " + String.valueOf(nbt));
      }
   }

   public boolean hasEffects() {
      return this.effects != null && !this.effects.isEmpty();
   }

   void safelyAddEffects(Iterable<?> collection) {
      if (collection != null && (!(collection instanceof Collection) || !((Collection)collection).isEmpty())) {
         List<FireworkEffect> effects = this.effects;
         if (effects == null) {
            effects = this.effects = new ArrayList();
         }

         Iterator var3 = collection.iterator();

         while(var3.hasNext()) {
            Object obj = var3.next();
            Preconditions.checkArgument(obj instanceof FireworkEffect, "%s in %s is not a FireworkEffect", obj, collection);
            effects.add((FireworkEffect)obj);
         }

      }
   }

   void applyToItem(CraftMetaItem.Applicator itemTag) {
      super.applyToItem(itemTag);
      if (!this.isFireworkEmpty()) {
         List<FireworkExplosion> effects = new ArrayList();
         if (this.hasEffects()) {
            Iterator var3 = this.effects.iterator();

            while(var3.hasNext()) {
               FireworkEffect effect = (FireworkEffect)var3.next();
               effects.add(getExplosion(effect));
            }
         }

         itemTag.put(FIREWORKS, new Fireworks(this.getPower(), effects));
      }
   }

   static IntList addColors(List<Color> colors) {
      if (colors.isEmpty()) {
         return IntList.of();
      } else {
         int[] colorArray = new int[colors.size()];
         int i = 0;

         Color color;
         for(Iterator var3 = colors.iterator(); var3.hasNext(); colorArray[i++] = color.asRGB()) {
            color = (Color)var3.next();
         }

         return IntList.of(colorArray);
      }
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isFireworkEmpty();
   }

   boolean isFireworkEmpty() {
      return !this.hasEffects() && !this.hasPower();
   }

   public boolean hasPower() {
      return this.power != null;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaFirework)) {
         return true;
      } else {
         boolean var10000;
         label38: {
            CraftMetaFirework that = (CraftMetaFirework)meta;
            if (Objects.equals(this.power, that.power)) {
               if (this.hasEffects()) {
                  if (that.hasEffects() && this.effects.equals(that.effects)) {
                     break label38;
                  }
               } else if (!that.hasEffects()) {
                  break label38;
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
      return super.notUncommon(meta) && (meta instanceof CraftMetaFirework || this.isFireworkEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasPower()) {
         hash = 61 * hash + this.power;
      }

      if (this.hasEffects()) {
         hash = 61 * hash + 13 * this.effects.hashCode();
      }

      return hash != original ? CraftMetaFirework.class.hashCode() ^ hash : hash;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasEffects()) {
         builder.put(EXPLOSIONS.BUKKIT, ImmutableList.copyOf(this.effects));
      }

      if (this.hasPower()) {
         builder.put(FLIGHT.BUKKIT, this.power);
      }

      return builder;
   }

   public CraftMetaFirework clone() {
      CraftMetaFirework meta = (CraftMetaFirework)super.clone();
      if (this.effects != null) {
         meta.effects = new ArrayList(this.effects);
      }

      return meta;
   }

   public void addEffect(FireworkEffect effect) {
      Preconditions.checkArgument(effect != null, "FireworkEffect cannot be null");
      if (this.effects == null) {
         this.effects = new ArrayList();
      }

      this.effects.add(effect);
   }

   public void addEffects(FireworkEffect... effects) {
      Preconditions.checkArgument(effects != null, "effects cannot be null");
      if (effects.length != 0) {
         List<FireworkEffect> list = this.effects;
         if (list == null) {
            list = this.effects = new ArrayList();
         }

         FireworkEffect[] var3 = effects;
         int var4 = effects.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            FireworkEffect effect = var3[var5];
            Preconditions.checkArgument(effect != null, "effects cannot contain null FireworkEffect");
            list.add(effect);
         }

      }
   }

   public void addEffects(Iterable<FireworkEffect> effects) {
      Preconditions.checkArgument(effects != null, "effects cannot be null");
      this.safelyAddEffects(effects);
   }

   public List<FireworkEffect> getEffects() {
      return this.effects == null ? ImmutableList.of() : ImmutableList.copyOf(this.effects);
   }

   public int getEffectsSize() {
      return this.effects == null ? 0 : this.effects.size();
   }

   public void removeEffect(int index) {
      if (this.effects == null) {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      } else {
         this.effects.remove(index);
      }
   }

   public void clearEffects() {
      this.effects = null;
   }

   public int getPower() {
      return this.hasPower() ? this.power : 0;
   }

   public void setPower(int power) {
      Preconditions.checkArgument(power >= 0, "power cannot be less than zero: %s", power);
      Preconditions.checkArgument(power <= 255, "power cannot be more than 255: %s", power);
      this.power = power;
   }

   static {
      FIREWORKS = new CraftMetaItem.ItemMetaKeyType(DataComponents.FIREWORKS, "Fireworks");
      FLIGHT = new ItemMetaKey("power");
      EXPLOSIONS = new ItemMetaKey("firework-effects");
   }
}
