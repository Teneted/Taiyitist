package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Endermite;

public class CraftEndermite extends CraftMonster implements Endermite {
   public CraftEndermite(CraftServer server, net.minecraft.world.entity.monster.Endermite entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Endermite getHandle() {
      return (net.minecraft.world.entity.monster.Endermite)super.getHandle();
   }

   public String toString() {
      return "CraftEndermite";
   }

   public boolean isPlayerSpawned() {
      return false;
   }

   public void setPlayerSpawned(boolean playerSpawned) {
   }
}
