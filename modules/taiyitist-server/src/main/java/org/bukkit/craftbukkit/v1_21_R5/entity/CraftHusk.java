package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Husk;

public class CraftHusk extends CraftZombie implements Husk {
   public CraftHusk(CraftServer server, net.minecraft.world.entity.monster.Husk entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftHusk";
   }
}
