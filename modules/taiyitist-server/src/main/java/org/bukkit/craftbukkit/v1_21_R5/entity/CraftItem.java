package org.bukkit.craftbukkit.v1_21_R5.entity;

import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class CraftItem extends CraftEntity implements Item {
   public CraftItem(CraftServer server, ItemEntity entity) {
      super(server, entity);
   }

   public ItemEntity getHandle() {
      return (ItemEntity)this.entity;
   }

   public ItemStack getItemStack() {
      return CraftItemStack.asCraftMirror(this.getHandle().getItem());
   }

   public void setItemStack(ItemStack stack) {
      this.getHandle().setItem(CraftItemStack.asNMSCopy(stack));
   }

   public int getPickupDelay() {
      return this.getHandle().pickupDelay;
   }

   public void setPickupDelay(int delay) {
      this.getHandle().pickupDelay = Math.min(delay, 32767);
   }

   public void setUnlimitedLifetime(boolean unlimited) {
      if (unlimited) {
         this.getHandle().age = -32768;
      } else {
         this.getHandle().age = this.getTicksLived();
      }

   }

   public boolean isUnlimitedLifetime() {
      return this.getHandle().age == -32768;
   }

   public void setTicksLived(int value) {
      super.setTicksLived(value);
      if (!this.isUnlimitedLifetime()) {
         this.getHandle().age = value;
      }

   }

   public void setOwner(UUID uuid) {
      this.getHandle().setTarget(uuid);
   }

   public UUID getOwner() {
      return this.getHandle().target;
   }

   public void setThrower(UUID uuid) {
      this.getHandle().thrower = uuid != null ? new EntityReference(uuid) : null;
   }

   public UUID getThrower() {
      EntityReference<Entity> thrower = this.getHandle().thrower;
      return thrower != null ? thrower.getUUID() : null;
   }

   public String toString() {
      return "CraftItem";
   }
}
