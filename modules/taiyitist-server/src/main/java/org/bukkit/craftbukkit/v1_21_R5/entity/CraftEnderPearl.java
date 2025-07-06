package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.EnderPearl;

public class CraftEnderPearl extends CraftThrowableProjectile implements EnderPearl {
   public CraftEnderPearl(CraftServer server, ThrownEnderpearl entity) {
      super(server, entity);
   }

   public ThrownEnderpearl getHandle() {
      return (ThrownEnderpearl)this.entity;
   }

   public String toString() {
      return "CraftEnderPearl";
   }
}
