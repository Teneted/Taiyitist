package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.LargeFireball;

public class CraftLargeFireball extends CraftSizedFireball implements LargeFireball {
   public CraftLargeFireball(CraftServer server, net.minecraft.world.entity.projectile.LargeFireball entity) {
      super(server, entity);
   }

   public void setYield(float yield) {
      super.setYield(yield);
      this.getHandle().explosionPower = (int)yield;
   }

   public net.minecraft.world.entity.projectile.LargeFireball getHandle() {
      return (net.minecraft.world.entity.projectile.LargeFireball)this.entity;
   }

   public String toString() {
      return "CraftLargeFireball";
   }
}
