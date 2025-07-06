package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import org.bukkit.craftbukkit.v1_21_R5.CraftLootTable;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public abstract class CraftMinecartContainer extends CraftMinecart implements Lootable {
   public CraftMinecartContainer(CraftServer server, AbstractMinecart entity) {
      super(server, entity);
   }

   public AbstractMinecartContainer getHandle() {
      return (AbstractMinecartContainer)this.entity;
   }

   public void setLootTable(LootTable table) {
      this.setLootTable(table, this.getSeed());
   }

   public LootTable getLootTable() {
      return CraftLootTable.minecraftToBukkit(this.getHandle().lootTable);
   }

   public void setSeed(long seed) {
      this.setLootTable(this.getLootTable(), seed);
   }

   public long getSeed() {
      return this.getHandle().lootTableSeed;
   }

   private void setLootTable(LootTable table, long seed) {
      this.getHandle().setLootTable(CraftLootTable.bukkitToMinecraft(table), seed);
   }
}
