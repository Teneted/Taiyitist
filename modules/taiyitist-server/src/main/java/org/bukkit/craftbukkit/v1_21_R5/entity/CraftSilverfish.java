package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
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
