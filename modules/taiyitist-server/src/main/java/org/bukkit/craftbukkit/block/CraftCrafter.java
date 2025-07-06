package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Crafter;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftCrafter extends CraftLootable<CrafterBlockEntity> implements Crafter {
   public CraftCrafter(World world, CrafterBlockEntity tileEntity) {
      super((World)world, (RandomizableContainerBlockEntity)tileEntity);
   }

   protected CraftCrafter(CraftCrafter state, Location location) {
      super((CraftLootable)state, (Location)location);
   }

   public Inventory getSnapshotInventory() {
      return new CraftInventory((Container)this.getSnapshot());
   }

   public Inventory getInventory() {
      return (Inventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventory((Container)this.getTileEntity()));
   }

   public CraftCrafter copy() {
      return new CraftCrafter(this, (Location)null);
   }

   public CraftCrafter copy(Location location) {
      return new CraftCrafter(this, location);
   }

   public int getCraftingTicks() {
      return ((CrafterBlockEntity)this.getSnapshot()).craftingTicksRemaining;
   }

   public void setCraftingTicks(int ticks) {
      ((CrafterBlockEntity)this.getSnapshot()).setCraftingTicksRemaining(ticks);
   }

   public boolean isSlotDisabled(int slot) {
      Preconditions.checkArgument(slot >= 0 && slot < 9, "Invalid slot index %s for Crafter", slot);
      return ((CrafterBlockEntity)this.getSnapshot()).isSlotDisabled(slot);
   }

   public void setSlotDisabled(int slot, boolean disabled) {
      Preconditions.checkArgument(slot >= 0 && slot < 9, "Invalid slot index %s for Crafter", slot);
      ((CrafterBlockEntity)this.getSnapshot()).setSlotState(slot, disabled);
   }

   public boolean isTriggered() {
      return ((CrafterBlockEntity)this.getSnapshot()).isTriggered();
   }

   public void setTriggered(boolean triggered) {
      ((CrafterBlockEntity)this.getSnapshot()).setTriggered(triggered);
   }
}
