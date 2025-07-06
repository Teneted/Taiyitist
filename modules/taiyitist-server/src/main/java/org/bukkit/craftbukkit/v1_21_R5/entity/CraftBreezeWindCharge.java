package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.BreezeWindCharge;

public class CraftBreezeWindCharge extends CraftAbstractWindCharge implements BreezeWindCharge {
   public CraftBreezeWindCharge(CraftServer server, net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge getHandle() {
      return (net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge)super.getHandle();
   }

   public String toString() {
      return "CraftBreezeWindCharge";
   }
}
