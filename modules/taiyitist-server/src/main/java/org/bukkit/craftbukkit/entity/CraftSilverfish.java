package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Silverfish;

public class CraftSilverfish extends CraftMonster implements Silverfish {
   public CraftSilverfish(CraftServer server, net.minecraft.world.entity.monster.Silverfish entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Silverfish getHandle() {
      return (net.minecraft.world.entity.monster.Silverfish)this.entity;
   }

   public String toString() {
      return "CraftSilverfish";
   }
}
