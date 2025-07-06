package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import org.bukkit.craftbukkit.v1_21_R5.CraftLootTable;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventory;
import org.bukkit.entity.ChestBoat;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootTable;

public abstract class CraftChestBoat extends CraftBoat implements ChestBoat {
   private final Inventory inventory;

   public CraftChestBoat(CraftServer server, AbstractChestBoat entity) {
      super(server, entity);
      this.inventory = new CraftInventory(entity);
   }

   public AbstractChestBoat getHandle() {
      return (AbstractChestBoat)this.entity;
   }

   public String toString() {
      return "CraftChestBoat";
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public void setLootTable(LootTable table) {
      this.setLootTable(table, this.getSeed());
   }

   public LootTable getLootTable() {
      return CraftLootTable.minecraftToBukkit(this.getHandle().getContainerLootTable());
   }

   public void setSeed(long seed) {
      this.setLootTable(this.getLootTable(), seed);
   }

   public long getSeed() {
      return this.getHandle().getContainerLootTableSeed();
   }

   private void setLootTable(LootTable table, long seed) {
      this.getHandle().setContainerLootTable(CraftLootTable.bukkitToMinecraft(table));
      this.getHandle().setContainerLootTableSeed(seed);
   }
}
