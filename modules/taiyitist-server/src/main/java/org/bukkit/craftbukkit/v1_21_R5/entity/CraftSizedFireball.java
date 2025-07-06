package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.projectile.Fireball;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.entity.SizedFireball;
import org.bukkit.inventory.ItemStack;

public class CraftSizedFireball extends CraftFireball implements SizedFireball {
   public CraftSizedFireball(CraftServer server, Fireball entity) {
      super(server, entity);
   }

   public ItemStack getDisplayItem() {
      return this.getHandle().getItem().isEmpty() ? new ItemStack(Material.FIRE_CHARGE) : CraftItemStack.asBukkitCopy(this.getHandle().getItem());
   }

   public void setDisplayItem(ItemStack item) {
      this.getHandle().setItem(CraftItemStack.asNMSCopy(item));
   }

   public Fireball getHandle() {
      return (Fireball)this.entity;
   }
}
