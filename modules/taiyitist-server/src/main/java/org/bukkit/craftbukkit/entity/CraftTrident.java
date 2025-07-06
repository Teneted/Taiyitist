package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.projectile.ThrownTrident;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

public class CraftTrident extends CraftAbstractArrow implements Trident {
   public CraftTrident(CraftServer server, ThrownTrident entity) {
      super(server, entity);
   }

   public ThrownTrident getHandle() {
      return (ThrownTrident)super.getHandle();
   }

   public ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(this.getHandle().pickupItemStack);
   }

   public void setItem(ItemStack itemStack) {
      this.getHandle().pickupItemStack = CraftItemStack.asNMSCopy(itemStack);
   }

   public String toString() {
      return "CraftTrident";
   }
}
