package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.entity.Pillager;
import org.bukkit.inventory.Inventory;

public class CraftPillager extends CraftIllager implements Pillager {
   public CraftPillager(CraftServer server, net.minecraft.world.entity.monster.Pillager entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Pillager getHandle() {
      return (net.minecraft.world.entity.monster.Pillager)super.getHandle();
   }

   public String toString() {
      return "CraftPillager";
   }

   public Inventory getInventory() {
      return new CraftInventory(this.getHandle().inventory);
   }
}
