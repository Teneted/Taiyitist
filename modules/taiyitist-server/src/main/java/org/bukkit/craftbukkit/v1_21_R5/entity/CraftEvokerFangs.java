package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;

public class CraftEvokerFangs extends CraftEntity implements EvokerFangs {
   public CraftEvokerFangs(CraftServer server, net.minecraft.world.entity.projectile.EvokerFangs entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.projectile.EvokerFangs getHandle() {
      return (net.minecraft.world.entity.projectile.EvokerFangs)super.getHandle();
   }

   public String toString() {
      return "CraftEvokerFangs";
   }

   public LivingEntity getOwner() {
      net.minecraft.world.entity.LivingEntity owner = this.getHandle().getOwner();
      return owner == null ? null : (LivingEntity)owner.getBukkitEntity();
   }

   public void setOwner(LivingEntity owner) {
      this.getHandle().setOwner(owner == null ? null : ((CraftLivingEntity)owner).getHandle());
   }

   public int getAttackDelay() {
      return this.getHandle().warmupDelayTicks;
   }

   public void setAttackDelay(int delay) {
      Preconditions.checkArgument(delay >= 0, "Delay must be positive");
      this.getHandle().warmupDelayTicks = delay;
   }
}
