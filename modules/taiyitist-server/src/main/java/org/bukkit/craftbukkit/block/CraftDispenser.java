package org.bukkit.craftbukkit.block;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.BlockProjectileSource;

public class CraftDispenser extends CraftLootable<DispenserBlockEntity> implements Dispenser {
   public CraftDispenser(World world, DispenserBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftDispenser(CraftDispenser state, Location location) {
      super((CraftLootable)state, (Location)location);
   }

   public Inventory getSnapshotInventory() {
      return new CraftInventory((Container)this.getSnapshot());
   }

   public Inventory getInventory() {
      return (Inventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventory((Container)this.getTileEntity()));
   }

   public BlockProjectileSource getBlockProjectileSource() {
      Block block = this.getBlock();
      return block.getType() != Material.DISPENSER ? null : new CraftBlockProjectileSource((DispenserBlockEntity)this.getTileEntityFromWorld());
   }

   public boolean dispense() {
      this.ensureNoWorldGeneration();
      Block block = this.getBlock();
      if (block.getType() == Material.DISPENSER) {
         CraftWorld world = (CraftWorld)this.getWorld();
         DispenserBlock dispense = (DispenserBlock)Blocks.DISPENSER;
         dispense.dispenseFrom(world.getHandle(), this.getHandle(), this.getPosition());
         return true;
      } else {
         return false;
      }
   }

   public CraftDispenser copy() {
      return new CraftDispenser(this, (Location)null);
   }

   public CraftDispenser copy(Location location) {
      return new CraftDispenser(this, location);
   }
}
