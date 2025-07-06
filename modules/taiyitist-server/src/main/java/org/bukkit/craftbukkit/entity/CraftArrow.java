package org.bukkit.craftbukkit.entity;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.Color;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class CraftArrow extends CraftAbstractArrow implements Arrow {
   public CraftArrow(CraftServer server, net.minecraft.world.entity.projectile.Arrow entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.projectile.Arrow getHandle() {
      return (net.minecraft.world.entity.projectile.Arrow)this.entity;
   }

   public String toString() {
      return "CraftTippedArrow";
   }

   public boolean addCustomEffect(PotionEffect effect, boolean override) {
      if (this.hasCustomEffect(effect.getType())) {
         if (!override) {
            return false;
         }

         this.removeCustomEffect(effect.getType());
      }

      this.getHandle().addEffect(CraftPotionUtil.fromBukkit(effect));
      this.getHandle().updateColor();
      return true;
   }

   public void clearCustomEffects() {
      PotionContents old = this.getHandle().getPotionContents();
      this.getHandle().setPotionContents(new PotionContents(old.potion(), old.customColor(), List.of(), old.customName()));
      this.getHandle().updateColor();
   }

   public List<PotionEffect> getCustomEffects() {
      ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
      Iterator var2 = this.getHandle().getPotionContents().customEffects().iterator();

      while(var2.hasNext()) {
         MobEffectInstance effect = (MobEffectInstance)var2.next();
         builder.add(CraftPotionUtil.toBukkit(effect));
      }

      return builder.build();
   }

   public boolean hasCustomEffect(PotionEffectType type) {
      Iterator var2 = this.getHandle().getPotionContents().customEffects().iterator();

      MobEffectInstance effect;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         effect = (MobEffectInstance)var2.next();
      } while(!CraftPotionUtil.equals(effect.getEffect(), type));

      return true;
   }

   public boolean hasCustomEffects() {
      return !this.getHandle().getPotionContents().customEffects().isEmpty();
   }

   public boolean removeCustomEffect(PotionEffectType effect) {
      if (!this.hasCustomEffect(effect)) {
         return false;
      } else {
         Holder<MobEffect> minecraft = CraftPotionEffectType.bukkitToMinecraftHolder(effect);
         PotionContents old = this.getHandle().getPotionContents();
         this.getHandle().setPotionContents(new PotionContents(old.potion(), old.customColor(), old.customEffects().stream().filter((mobEffect) -> {
            return !mobEffect.getEffect().equals(minecraft);
         }).toList(), old.customName()));
         return true;
      }
   }

   public void setBasePotionData(PotionData data) {
      this.setBasePotionType(CraftPotionUtil.fromBukkit(data));
   }

   public PotionData getBasePotionData() {
      return CraftPotionUtil.toBukkit(this.getBasePotionType());
   }

   public void setBasePotionType(PotionType potionType) {
      if (potionType != null) {
         this.getHandle().setPotionContents(this.getHandle().getPotionContents().withPotion(CraftPotionType.bukkitToMinecraftHolder(potionType)));
      } else {
         PotionContents old = this.getHandle().getPotionContents();
         this.getHandle().setPotionContents(new PotionContents(Optional.empty(), old.customColor(), old.customEffects(), old.customName()));
      }

   }

   public PotionType getBasePotionType() {
      return (PotionType)this.getHandle().getPotionContents().potion().map(CraftPotionType::minecraftHolderToBukkit).orElse((Object)null);
   }

   public void setColor(Color color) {
      int colorRGB = color == null ? -1 : color.asRGB();
      PotionContents old = this.getHandle().getPotionContents();
      this.getHandle().setPotionContents(new PotionContents(old.potion(), Optional.of(colorRGB), old.customEffects(), old.customName()));
   }

   public Color getColor() {
      return this.getHandle().getColor() <= -1 ? null : Color.fromRGB(this.getHandle().getColor());
   }
}
