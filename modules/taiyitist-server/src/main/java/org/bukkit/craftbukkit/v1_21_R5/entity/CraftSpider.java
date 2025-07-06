package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Spider;

public class CraftSpider extends CraftMonster implements Spider {
   public CraftSpider(CraftServer server, net.minecraft.world.entity.monster.Spider entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Spider getHandle() {
      return (net.minecraft.world.entity.monster.Spider)this.entity;
   }

   public String toString() {
      return "CraftSpider";
   }
}
