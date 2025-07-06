package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftHopper extends CraftLootable<HopperBlockEntity> implements Hopper {
   public CraftHopper(World world, HopperBlockEntity tileEntity) {
      super((World)world, (RandomizableContainerBlockEntity)tileEntity);
   }

   protected CraftHopper(CraftHopper state, Location location) {
      super((CraftLootable)state, (Location)location);
   }

   public Inventory getSnapshotInventory() {
      return new CraftInventory((Container)this.getSnapshot());
   }

   public Inventory getInventory() {
      return (Inventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventory((Container)this.getTileEntity()));
   }

   public CraftHopper copy() {
      return new CraftHopper(this, (Location)null);
   }

   public CraftHopper copy(Location location) {
      return new CraftHopper(this, location);
   }
}
