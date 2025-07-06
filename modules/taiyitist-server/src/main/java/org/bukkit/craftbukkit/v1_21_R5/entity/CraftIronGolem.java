package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.IronGolem;

public class CraftIronGolem extends CraftGolem implements IronGolem {
   public CraftIronGolem(CraftServer server, net.minecraft.world.entity.animal.IronGolem entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.IronGolem getHandle() {
      return (net.minecraft.world.entity.animal.IronGolem)this.entity;
   }

   public String toString() {
      return "CraftIronGolem";
   }

   public boolean isPlayerCreated() {
      return this.getHandle().isPlayerCreated();
   }

   public void setPlayerCreated(boolean playerCreated) {
      this.getHandle().setPlayerCreated(playerCreated);
   }
}
