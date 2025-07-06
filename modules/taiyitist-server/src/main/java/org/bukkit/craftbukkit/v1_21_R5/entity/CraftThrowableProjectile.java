package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.inventory.ItemStack;

public abstract class CraftThrowableProjectile extends CraftProjectile implements ThrowableProjectile {
   public CraftThrowableProjectile(CraftServer server, ThrowableItemProjectile entity) {
      super(server, entity);
   }

   public ItemStack getItem() {
      return this.getHandle().getItem().isEmpty() ? CraftItemStack.asBukkitCopy(new net.minecraft.world.item.ItemStack(this.getHandle().getDefaultItemPublic())) : CraftItemStack.asBukkitCopy(this.getHandle().getItem());
   }

   public void setItem(ItemStack item) {
      this.getHandle().setItem(CraftItemStack.asNMSCopy(item));
   }

   public ThrowableItemProjectile getHandle() {
      return (ThrowableItemProjectile)this.entity;
   }
}
