package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.HappyGhast;

public class CraftHappyGhast extends CraftAnimals implements HappyGhast {
   public CraftHappyGhast(CraftServer server, net.minecraft.world.entity.animal.HappyGhast entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.HappyGhast getHandle() {
      return (net.minecraft.world.entity.animal.HappyGhast)this.entity;
   }

   public String toString() {
      return "CraftHappyGhast";
   }
}
