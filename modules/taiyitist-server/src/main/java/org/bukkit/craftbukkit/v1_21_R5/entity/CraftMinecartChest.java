package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.vehicle.MinecartChest;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;

public class CraftMinecartChest extends CraftMinecartContainer implements StorageMinecart {
   private final CraftInventory inventory;

   public CraftMinecartChest(CraftServer server, MinecartChest entity) {
      super(server, entity);
      this.inventory = new CraftInventory(entity);
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public String toString() {
      return "CraftMinecartChest{inventory=" + String.valueOf(this.inventory) + "}";
   }
}
