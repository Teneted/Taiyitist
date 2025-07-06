package org.bukkit.craftbukkit.v1_21_R5.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryHorse extends CraftInventoryAbstractHorse implements HorseInventory {
   public CraftInventoryHorse(Container inventory, EntityEquipment equipment) {
      super(inventory, equipment);
   }

   public ItemStack getArmor() {
      net.minecraft.world.item.ItemStack item = this.equipment.get(EquipmentSlot.BODY);
      return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
   }

   public void setArmor(ItemStack stack) {
      this.equipment.set(EquipmentSlot.BODY, CraftItemStack.asNMSCopy(stack));
   }
}
