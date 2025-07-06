package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.SmallFireball;

public class CraftSmallFireball extends CraftSizedFireball implements SmallFireball {
   public CraftSmallFireball(CraftServer server, net.minecraft.world.entity.projectile.SmallFireball entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.projectile.SmallFireball getHandle() {
      return (net.minecraft.world.entity.projectile.SmallFireball)this.entity;
   }

   public String toString() {
      return "CraftSmallFireball";
   }
}
