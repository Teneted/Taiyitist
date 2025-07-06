package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Ravager;

public class CraftRavager extends CraftRaider implements Ravager {
   public CraftRavager(CraftServer server, net.minecraft.world.entity.monster.Ravager entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Ravager getHandle() {
      return (net.minecraft.world.entity.monster.Ravager)super.getHandle();
   }

   public String toString() {
      return "CraftRavager";
   }
}
