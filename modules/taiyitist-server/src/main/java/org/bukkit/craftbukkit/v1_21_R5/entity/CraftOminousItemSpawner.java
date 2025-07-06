package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.entity.OminousItemSpawner;
import org.bukkit.inventory.ItemStack;

public class CraftOminousItemSpawner extends CraftEntity implements OminousItemSpawner {
   public CraftOminousItemSpawner(CraftServer server, net.minecraft.world.entity.OminousItemSpawner entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.OminousItemSpawner getHandle() {
      return (net.minecraft.world.entity.OminousItemSpawner)this.entity;
   }

   public String toString() {
      return "CraftOminousItemSpawner";
   }

   public ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(this.getHandle().getItem());
   }

   public void setItem(ItemStack item) {
      this.getHandle().setItem(CraftItemStack.asNMSCopy(item));
   }

   public long getSpawnItemAfterTicks() {
      return this.getHandle().spawnItemAfterTicks;
   }

   public void setSpawnItemAfterTicks(long ticks) {
      this.getHandle().spawnItemAfterTicks = ticks;
   }
}
