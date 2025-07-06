package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.entity.MushroomCow;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftMushroomCow extends CraftAbstractCow implements MushroomCow {
   public CraftMushroomCow(CraftServer server, net.minecraft.world.entity.animal.MushroomCow entity) {
      super(server, entity);
   }

   public boolean hasEffectsForNextStew() {
      SuspiciousStewEffects stewEffects = this.getHandle().stewEffects;
      return stewEffects != null && !stewEffects.effects().isEmpty();
   }

   public List<PotionEffect> getEffectsForNextStew() {
      SuspiciousStewEffects stewEffects = this.getHandle().stewEffects;
      return (List)(stewEffects != null ? stewEffects.effects().stream().map((recordSuspiciousEffect) -> {
         return CraftPotionUtil.toBukkit(recordSuspiciousEffect.createEffectInstance());
      }).toList() : ImmutableList.of());
   }

   public boolean addEffectToNextStew(PotionEffect potionEffect, boolean overwrite) {
      Preconditions.checkArgument(potionEffect != null, "PotionEffect cannot be null");
      MobEffectInstance minecraftPotionEffect = CraftPotionUtil.fromBukkit(potionEffect);
      if (!overwrite && this.hasEffectForNextStew(potionEffect.getType())) {
         return false;
      } else {
         SuspiciousStewEffects stewEffects = this.getHandle().stewEffects;
         if (stewEffects == null) {
            stewEffects = SuspiciousStewEffects.EMPTY;
         }

         SuspiciousStewEffects.Entry recordSuspiciousEffect = new SuspiciousStewEffects.Entry(minecraftPotionEffect.getEffect(), minecraftPotionEffect.getDuration());
         this.removeEffectFromNextStew(potionEffect.getType());
         this.getHandle().stewEffects = stewEffects.withEffectAdded(recordSuspiciousEffect);
         return true;
      }
   }

   public boolean removeEffectFromNextStew(PotionEffectType potionEffectType) {
      Preconditions.checkArgument(potionEffectType != null, "potionEffectType cannot be null");
      if (!this.hasEffectForNextStew(potionEffectType)) {
         return false;
      } else {
         SuspiciousStewEffects stewEffects = this.getHandle().stewEffects;
         if (stewEffects == null) {
            return false;
         } else {
            Holder<MobEffect> minecraftPotionEffectType = CraftPotionEffectType.bukkitToMinecraftHolder(potionEffectType);
            this.getHandle().stewEffects = new SuspiciousStewEffects(stewEffects.effects().stream().filter((effect) -> {
               return !effect.effect().equals(minecraftPotionEffectType);
            }).toList());
            return true;
         }
      }
   }

   public boolean hasEffectForNextStew(PotionEffectType potionEffectType) {
      Preconditions.checkArgument(potionEffectType != null, "potionEffectType cannot be null");
      SuspiciousStewEffects stewEffects = this.getHandle().stewEffects;
      if (stewEffects == null) {
         return false;
      } else {
         Holder<MobEffect> minecraftPotionEffectType = CraftPotionEffectType.bukkitToMinecraftHolder(potionEffectType);
         return stewEffects.effects().stream().anyMatch((recordSuspiciousEffect) -> {
            return recordSuspiciousEffect.effect().equals(minecraftPotionEffectType);
         });
      }
   }

   public void clearEffectsForNextStew() {
      this.getHandle().stewEffects = null;
   }

   public net.minecraft.world.entity.animal.MushroomCow getHandle() {
      return (net.minecraft.world.entity.animal.MushroomCow)this.entity;
   }

   public MushroomCow.Variant getVariant() {
      return Variant.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setVariant(MushroomCow.Variant variant) {
      Preconditions.checkArgument(variant != null, "Variant cannot be null");
      this.getHandle().setVariant(net.minecraft.world.entity.animal.MushroomCow.Variant.values()[variant.ordinal()]);
   }

   public String toString() {
      return "CraftMushroomCow";
   }
}
