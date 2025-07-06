package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Snowball;

public class CraftSnowball extends CraftThrowableProjectile implements Snowball {
   public CraftSnowball(CraftServer server, net.minecraft.world.entity.projectile.Snowball entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.projectile.Snowball getHandle() {
      return (net.minecraft.world.entity.projectile.Snowball)this.entity;
   }

   public String toString() {
      return "CraftSnowball";
   }
}
