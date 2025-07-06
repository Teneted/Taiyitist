package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.WitherSkull;

public class CraftWitherSkull extends CraftFireball implements WitherSkull {
   public CraftWitherSkull(CraftServer server, net.minecraft.world.entity.projectile.WitherSkull entity) {
      super(server, entity);
   }

   public void setCharged(boolean charged) {
      this.getHandle().setDangerous(charged);
   }

   public boolean isCharged() {
      return this.getHandle().isDangerous();
   }

   public net.minecraft.world.entity.projectile.WitherSkull getHandle() {
      return (net.minecraft.world.entity.projectile.WitherSkull)this.entity;
   }

   public String toString() {
      return "CraftWitherSkull";
   }
}
