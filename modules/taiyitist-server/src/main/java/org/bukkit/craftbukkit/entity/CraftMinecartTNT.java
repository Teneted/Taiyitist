package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.minecart.ExplosiveMinecart;

public final class CraftMinecartTNT extends CraftMinecart implements ExplosiveMinecart {
   CraftMinecartTNT(CraftServer server, MinecartTNT entity) {
      super(server, entity);
   }

   public float getYield() {
      return this.getHandle().explosionPowerBase;
   }

   public boolean isIncendiary() {
      return this.getHandle().bridge$isIncendiary();
   }

   public void setIsIncendiary(boolean isIncendiary) {
      this.getHandle().isIncendiary = isIncendiary;
   }

   public void setYield(float yield) {
      this.getHandle().explosionPowerBase = yield;
   }

   public float getExplosionSpeedFactor() {
      return this.getHandle().explosionSpeedFactor;
   }

   public void setExplosionSpeedFactor(float factor) {
      this.getHandle().explosionSpeedFactor = factor;
   }

   public void setFuseTicks(int ticks) {
      this.getHandle().fuse = ticks;
   }

   public int getFuseTicks() {
      return this.getHandle().getFuse();
   }

   public void ignite() {
      this.getHandle().primeFuse((DamageSource)null);
   }

   public boolean isIgnited() {
      return this.getHandle().isPrimed();
   }

   public void explode() {
      this.getHandle().explode(this.getHandle().getDeltaMovement().horizontalDistanceSqr());
   }

   public void explode(double power) {
      Preconditions.checkArgument(0.0 <= power && power <= 5.0, "Power must be in range [0, 5] (got %s)", power);
      this.getHandle().explode(power);
   }

   public MinecartTNT getHandle() {
      return (MinecartTNT)super.getHandle();
   }

   public String toString() {
      return "CraftMinecartTNT";
   }
}
