package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftDropper extends CraftLootable<DropperBlockEntity> implements Dropper {
   public CraftDropper(World world, DropperBlockEntity tileEntity) {
      super((World)world, (RandomizableContainerBlockEntity)tileEntity);
   }

   protected CraftDropper(CraftDropper state, Location location) {
      super((CraftLootable)state, (Location)location);
   }

   public Inventory getSnapshotInventory() {
      return new CraftInventory((Container)this.getSnapshot());
   }

   public Inventory getInventory() {
      return (Inventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventory((Container)this.getTileEntity()));
   }

   public void drop() {
      this.ensureNoWorldGeneration();
      Block block = this.getBlock();
      if (block.getType() == Material.DROPPER) {
         CraftWorld world = (CraftWorld)this.getWorld();
         DropperBlock drop = (DropperBlock)Blocks.DROPPER;
         drop.dispenseFrom(world.getHandle(), this.getHandle(), this.getPosition());
      }

   }

   public CraftDropper copy() {
      return new CraftDropper(this, (Location)null);
   }

   public CraftDropper copy(Location location) {
      return new CraftDropper(this, location);
   }
}
