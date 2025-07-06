package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Giant;

public class CraftGiant extends CraftMonster implements Giant {
   public CraftGiant(CraftServer server, net.minecraft.world.entity.monster.Giant entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Giant getHandle() {
      return (net.minecraft.world.entity.monster.Giant)this.entity;
   }

   public String toString() {
      return "CraftGiant";
   }
}
