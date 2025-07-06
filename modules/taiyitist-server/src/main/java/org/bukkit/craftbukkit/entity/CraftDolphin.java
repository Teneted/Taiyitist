package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
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
