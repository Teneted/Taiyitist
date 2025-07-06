package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
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
