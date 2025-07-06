package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Lectern;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryLectern;
import org.bukkit.inventory.Inventory;

public class CraftLectern extends CraftBlockEntityState<LecternBlockEntity> implements Lectern {
   public CraftLectern(World world, LecternBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftLectern(CraftLectern state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public int getPage() {
      return ((LecternBlockEntity)this.getSnapshot()).getPage();
   }

   public void setPage(int page) {
      ((LecternBlockEntity)this.getSnapshot()).setPage(page);
   }

   public Inventory getSnapshotInventory() {
      return new CraftInventoryLectern(((LecternBlockEntity)this.getSnapshot()).bookAccess);
   }

   public Inventory getInventory() {
      return (Inventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventoryLectern(((LecternBlockEntity)this.getTileEntity()).bookAccess));
   }

   public boolean update(boolean force, boolean applyPhysics) {
      boolean result = super.update(force, applyPhysics);
      if (result && this.getType() == Material.LECTERN && this.getWorldHandle() instanceof Level) {
         LecternBlock.signalPageChange(this.world.getHandle(), this.getPosition(), this.getHandle());
      }

      return result;
   }

   public CraftLectern copy() {
      return new CraftLectern(this, (Location)null);
   }

   public CraftLectern copy(Location location) {
      return new CraftLectern(this, location);
   }
}
