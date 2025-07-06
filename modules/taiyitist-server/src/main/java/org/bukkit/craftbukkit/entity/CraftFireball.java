package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Fireball;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CraftFireball extends CraftProjectile implements Fireball {
   public CraftFireball(CraftServer server, AbstractHurtingProjectile entity) {
      super(server, entity);
   }

   public float getYield() {
      return this.getHandle().bridge$bukkitYield();
   }

   public boolean isIncendiary() {
      return this.getHandle().bridge$isIncendiary();
   }

   public void setIsIncendiary(boolean isIncendiary) {
      this.getHandle().banner$setIsIncendiary(isIncendiary);
   }

   public void setYield(float yield) {
      this.getHandle().banner$setBukkitYield(yield);
   }

   public ProjectileSource getShooter() {
      return this.getHandle().bridge$projectileSource();
   }

   public void setShooter(ProjectileSource shooter) {
      if (shooter instanceof CraftLivingEntity) {
         this.getHandle().setOwner(((CraftLivingEntity)shooter).getHandle());
      } else {
         this.getHandle().setOwner((Entity)null);
      }

      this.getHandle().banner$setProjectileSource(shooter);
   }

   public Vector getDirection() {
      return this.getAcceleration();
   }

   public void setDirection(Vector direction) {
      Preconditions.checkArgument(direction != null, "Vector direction cannot be null");
      if (direction.isZero()) {
         this.setVelocity(direction);
         this.setAcceleration(direction);
      } else {
         direction = direction.clone().normalize();
         this.setVelocity(direction.clone().multiply(this.getVelocity().length()));
         this.setAcceleration(direction.multiply(this.getAcceleration().length()));
      }
   }

   public void setAcceleration(@NotNull Vector acceleration) {
      Preconditions.checkArgument(acceleration != null, "Vector acceleration cannot be null");
      this.getHandle().assignDirectionalMovement(new Vec3(acceleration.getX(), acceleration.getY(), acceleration.getZ()), acceleration.length());
      this.update();
   }

   @NotNull
   public Vector getAcceleration() {
      Vec3 delta = this.getHandle().getDeltaMovement();
      return new Vector(delta.x, delta.y, delta.z);
   }

   public AbstractHurtingProjectile getHandle() {
      return (AbstractHurtingProjectile)this.entity;
   }

   public String toString() {
      return "CraftFireball";
   }
}
