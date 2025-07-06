package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Dolphin;

public class CraftDolphin extends CraftAgeable implements Dolphin {
   public CraftDolphin(CraftServer server, net.minecraft.world.entity.animal.Dolphin entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Dolphin getHandle() {
      return (net.minecraft.world.entity.animal.Dolphin)super.getHandle();
   }

   public String toString() {
      return "CraftDolphin";
   }
}
