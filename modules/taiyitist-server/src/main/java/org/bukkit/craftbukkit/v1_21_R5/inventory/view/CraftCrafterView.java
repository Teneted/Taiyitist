package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.world.inventory.CrafterMenu;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.CrafterInventory;
import org.bukkit.inventory.view.CrafterView;

public class CraftCrafterView extends CraftInventoryView<CrafterMenu, CrafterInventory> implements CrafterView {
   public CraftCrafterView(HumanEntity player, CrafterInventory viewing, CrafterMenu container) {
      super(player, viewing, container);
   }

   public boolean isSlotDisabled(int slot) {
      return ((CrafterMenu)this.container).isSlotDisabled(slot);
   }

   public boolean isPowered() {
      return ((CrafterMenu)this.container).isPowered();
   }

   public void setSlotDisabled(int slot, boolean disabled) {
      Preconditions.checkArgument(slot >= 0 && slot < 9, "Invalid slot index %s for Crafter", slot);
      ((CrafterMenu)this.container).setSlotState(slot, !disabled);
   }
}
