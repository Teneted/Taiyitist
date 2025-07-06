package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Horse.Variant;

public class CraftSkeletonHorse extends CraftAbstractHorse implements SkeletonHorse {
   public CraftSkeletonHorse(CraftServer server, net.minecraft.world.entity.animal.horse.SkeletonHorse entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftSkeletonHorse";
   }

   public Horse.Variant getVariant() {
      return Variant.SKELETON_HORSE;
   }

   public net.minecraft.world.entity.animal.horse.SkeletonHorse getHandle() {
      return (net.minecraft.world.entity.animal.horse.SkeletonHorse)this.entity;
   }

   public boolean isTrapped() {
      return this.getHandle().isTrap();
   }

   public void setTrapped(boolean trapped) {
      this.getHandle().setTrap(trapped);
   }

   public int getTrapTime() {
      return this.getHandle().trapTime;
   }

   public void setTrapTime(int trapTime) {
      this.getHandle().trapTime = trapTime;
   }
}
