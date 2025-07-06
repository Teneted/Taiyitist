package org.bukkit.craftbukkit.entity;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;

public class CraftAreaEffectCloud extends CraftEntity implements AreaEffectCloud {
   public CraftAreaEffectCloud(CraftServer server, net.minecraft.world.entity.AreaEffectCloud entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.AreaEffectCloud getHandle() {
      return (net.minecraft.world.entity.AreaEffectCloud)super.getHandle();
   }

   public String toString() {
      return "CraftAreaEffectCloud";
   }

   public int getDuration() {
      return this.getHandle().getDuration();
   }

   public void setDuration(int duration) {
      this.getHandle().setDuration(duration);
   }

   public int getWaitTime() {
      return this.getHandle().waitTime;
   }

   public void setWaitTime(int waitTime) {
      this.getHandle().setWaitTime(waitTime);
   }

   public int getReapplicationDelay() {
      return this.getHandle().reapplicationDelay;
   }

   public void setReapplicationDelay(int delay) {
      this.getHandle().reapplicationDelay = delay;
   }

   public int getDurationOnUse() {
      return this.getHandle().durationOnUse;
   }

   public void setDurationOnUse(int duration) {
      this.getHandle().durationOnUse = duration;
   }

   public float getRadius() {
      return this.getHandle().getRadius();
   }

   public void setRadius(float radius) {
      this.getHandle().setRadius(radius);
   }

   public float getRadiusOnUse() {
      return this.getHandle().radiusOnUse;
   }

   public void setRadiusOnUse(float radius) {
      this.getHandle().setRadiusOnUse(radius);
   }

   public float getRadiusPerTick() {
      return this.getHandle().radiusPerTick;
   }

   public void setRadiusPerTick(float radius) {
      this.getHandle().setRadiusPerTick(radius);
   }

   public Particle getParticle() {
      return CraftParticle.minecraftToBukkit(this.getHandle().getParticle().getType());
   }

   public void setParticle(Particle particle) {
      this.setParticle(particle, (Object)null);
   }

   public <T> void setParticle(Particle particle, T data) {
      this.getHandle().setCustomParticle(CraftParticle.createParticleParam(particle, data));
   }

   public Color getColor() {
      return Color.fromRGB(this.getHandle().potionContents.getColor());
   }

   public void setColor(Color color) {
      PotionContents old = this.getHandle().potionContents;
      this.getHandle().setPotionContents(new PotionContents(old.potion(), Optional.of(color.asRGB()), old.customEffects(), old.customName()));
   }

   public boolean addCustomEffect(PotionEffect effect, boolean override) {
      if (this.hasCustomEffect(effect.getType())) {
         if (!override) {
            return false;
         }

         this.removeCustomEffect(effect.getType());
      }

      this.getHandle().addEffect(CraftPotionUtil.fromBukkit(effect));
      this.getHandle().updateParticle();
      return true;
   }

   public void clearCustomEffects() {
      PotionContents old = this.getHandle().potionContents;
      this.getHandle().setPotionContents(new PotionContents(old.potion(), old.customColor(), List.of(), old.customName()));
      this.getHandle().updateParticle();
   }

   public List<PotionEffect> getCustomEffects() {
      ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
      Iterator var2 = this.getHandle().potionContents.customEffects().iterator();

      while(var2.hasNext()) {
         MobEffectInstance effect = (MobEffectInstance)var2.next();
         builder.add(CraftPotionUtil.toBukkit(effect));
      }

      return builder.build();
   }

   public boolean hasCustomEffect(PotionEffectType type) {
      Iterator var2 = this.getHandle().potionContents.customEffects().iterator();

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
      return !this.getHandle().potionContents.customEffects().isEmpty();
   }

   public boolean removeCustomEffect(PotionEffectType effect) {
      if (!this.hasCustomEffect(effect)) {
         return false;
      } else {
         Holder<MobEffect> minecraft = CraftPotionEffectType.bukkitToMinecraftHolder(effect);
         PotionContents old = this.getHandle().potionContents;
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
         this.getHandle().setPotionContents(this.getHandle().potionContents.withPotion(CraftPotionType.bukkitToMinecraftHolder(potionType)));
      } else {
         PotionContents old = this.getHandle().potionContents;
         this.getHandle().setPotionContents(new PotionContents(Optional.empty(), old.customColor(), old.customEffects(), old.customName()));
      }

   }

   public PotionType getBasePotionType() {
      return (PotionType)this.getHandle().potionContents.potion().map(CraftPotionType::minecraftHolderToBukkit).orElse((Object)null);
   }

   public ProjectileSource getSource() {
      LivingEntity source = this.getHandle().getOwner();
      return source == null ? null : (org.bukkit.entity.LivingEntity)source.getBukkitEntity();
   }

   public void setSource(ProjectileSource shooter) {
      if (shooter instanceof CraftLivingEntity craftLivingEntity) {
         this.getHandle().setOwner(craftLivingEntity.getHandle());
      } else {
         this.getHandle().setOwner((LivingEntity)null);
      }

   }
}
