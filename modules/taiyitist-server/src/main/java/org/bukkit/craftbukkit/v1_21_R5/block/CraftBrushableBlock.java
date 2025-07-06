package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BrushableBlock;
import org.bukkit.craftbukkit.v1_21_R5.CraftLootTable;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

public class CraftBrushableBlock extends CraftBlockEntityState<BrushableBlockEntity> implements BrushableBlock {
   public CraftBrushableBlock(World world, BrushableBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftBrushableBlock(CraftBrushableBlock state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(((BrushableBlockEntity)this.getSnapshot()).getItem());
   }

   public void setItem(ItemStack item) {
      ((BrushableBlockEntity)this.getSnapshot()).item = CraftItemStack.asNMSCopy(item);
   }

   protected void applyTo(BrushableBlockEntity lootable) {
      super.applyTo(lootable);
      if (((BrushableBlockEntity)this.getSnapshot()).lootTable == null) {
         lootable.setLootTable((ResourceKey)null, 0L);
      }

   }

   public LootTable getLootTable() {
      return CraftLootTable.minecraftToBukkit(((BrushableBlockEntity)this.getSnapshot()).lootTable);
   }

   public void setLootTable(LootTable table) {
      this.setLootTable(table, this.getSeed());
   }

   public long getSeed() {
      return ((BrushableBlockEntity)this.getSnapshot()).lootTableSeed;
   }

   public void setSeed(long seed) {
      this.setLootTable(this.getLootTable(), seed);
   }

   private void setLootTable(LootTable table, long seed) {
      ((BrushableBlockEntity)this.getSnapshot()).setLootTable(CraftLootTable.bukkitToMinecraft(table), seed);
   }

   public CraftBrushableBlock copy() {
      return new CraftBrushableBlock(this, (Location)null);
   }

   public CraftBrushableBlock copy(Location location) {
      return new CraftBrushableBlock(this, location);
   }
}
