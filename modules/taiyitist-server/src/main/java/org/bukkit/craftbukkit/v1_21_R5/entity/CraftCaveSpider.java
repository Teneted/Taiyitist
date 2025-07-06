package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.CaveSpider;

public class CraftCaveSpider extends CraftSpider implements CaveSpider {
   public CraftCaveSpider(CraftServer server, net.minecraft.world.entity.monster.CaveSpider entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.CaveSpider getHandle() {
      return (net.minecraft.world.entity.monster.CaveSpider)this.entity;
   }

   public String toString() {
      return "CraftCaveSpider";
   }
}
