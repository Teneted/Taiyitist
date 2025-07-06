package org.bukkit.craftbukkit.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public abstract class CraftLootable<T extends RandomizableContainerBlockEntity> extends CraftContainer<T> implements Nameable, Lootable {
   public CraftLootable(World world, T tileEntity) {
      super(world, tileEntity);
   }

   protected CraftLootable(CraftLootable<T> state, Location location) {
      super((CraftContainer)state, (Location)location);
   }

   protected void applyTo(T lootable) {
      super.applyTo((T) lootable);
      if (((RandomizableContainerBlockEntity)this.getSnapshot()).lootTable == null) {
         lootable.setLootTable((ResourceKey)null, 0L);
      }

   }

   public LootTable getLootTable() {
      return CraftLootTable.minecraftToBukkit(((RandomizableContainerBlockEntity)this.getSnapshot()).lootTable);
   }

   public void setLootTable(LootTable table) {
      this.setLootTable(table, this.getSeed());
   }

   public long getSeed() {
      return ((RandomizableContainerBlockEntity)this.getSnapshot()).lootTableSeed;
   }

   public void setSeed(long seed) {
      this.setLootTable(this.getLootTable(), seed);
   }

   private void setLootTable(LootTable table, long seed) {
      ((RandomizableContainerBlockEntity)this.getSnapshot()).setLootTable(CraftLootTable.bukkitToMinecraft(table), seed);
   }

   public abstract CraftLootable<T> copy();

   public abstract CraftLootable<T> copy(Location var1);
}
