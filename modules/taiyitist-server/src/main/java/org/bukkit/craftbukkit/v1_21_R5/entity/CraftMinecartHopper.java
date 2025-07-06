package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.vehicle.MinecartHopper;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.Inventory;

public final class CraftMinecartHopper extends CraftMinecartContainer implements HopperMinecart {
   private final CraftInventory inventory;

   public CraftMinecartHopper(CraftServer server, MinecartHopper entity) {
      super(server, entity);
      this.inventory = new CraftInventory(entity);
   }

   public String toString() {
      return "CraftMinecartHopper{inventory=" + String.valueOf(this.inventory) + "}";
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public boolean isEnabled() {
      return ((MinecartHopper)this.getHandle()).isEnabled();
   }

   public void setEnabled(boolean enabled) {
      ((MinecartHopper)this.getHandle()).setEnabled(enabled);
   }
}
