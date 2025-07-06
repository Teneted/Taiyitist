package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Monster;

public class CraftMonster extends CraftCreature implements Monster, CraftEnemy {
   public CraftMonster(CraftServer server, net.minecraft.world.entity.monster.Monster entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Monster getHandle() {
      return (net.minecraft.world.entity.monster.Monster)this.entity;
   }

   public String toString() {
      return "CraftMonster";
   }
}
