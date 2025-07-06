package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Cod;

public class CraftCod extends CraftFish implements Cod {
   public CraftCod(CraftServer server, net.minecraft.world.entity.animal.Cod entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Cod getHandle() {
      return (net.minecraft.world.entity.animal.Cod)super.getHandle();
   }

   public String toString() {
      return "CraftCod";
   }
}
