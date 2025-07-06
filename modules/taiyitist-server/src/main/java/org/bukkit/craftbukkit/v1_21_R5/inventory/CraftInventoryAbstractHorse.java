package org.bukkit.craftbukkit.v1_21_R5.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryAbstractHorse extends CraftInventory implements AbstractHorseInventory {
   protected final EntityEquipment equipment;

   public CraftInventoryAbstractHorse(Container inventory, EntityEquipment equipment) {
      super(inventory);
      this.equipment = equipment;
   }

   public ItemStack getSaddle() {
      net.minecraft.world.item.ItemStack item = this.equipment.get(EquipmentSlot.SADDLE);
      return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
   }

   public void setSaddle(ItemStack stack) {
      this.equipment.set(EquipmentSlot.SADDLE, CraftItemStack.asNMSCopy(stack));
   }
}
