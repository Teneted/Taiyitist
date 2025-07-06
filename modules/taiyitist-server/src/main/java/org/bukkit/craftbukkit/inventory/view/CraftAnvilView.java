package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.Nullable;

public class CraftAnvilView extends CraftInventoryView<AnvilMenu, AnvilInventory> implements AnvilView {
   public CraftAnvilView(HumanEntity player, AnvilInventory viewing, AnvilMenu container) {
      super(player, viewing, container);
   }

   @Nullable
   public String getRenameText() {
      return ((AnvilMenu)this.container).itemName;
   }

   public int getRepairItemCountCost() {
      return ((AnvilMenu)this.container).repairItemCountCost;
   }

   public int getRepairCost() {
      return ((AnvilMenu)this.container).getCost();
   }

   public int getMaximumRepairCost() {
      return ((AnvilMenu)this.container).maximumRepairCost;
   }

   public void setRepairItemCountCost(int cost) {
      ((AnvilMenu)this.container).repairItemCountCost = cost;
   }

   public void setRepairCost(int cost) {
      ((AnvilMenu)this.container).cost.set(cost);
   }

   public void setMaximumRepairCost(int cost) {
      ((AnvilMenu)this.container).maximumRepairCost = cost;
   }

   public void updateFromLegacy(CraftInventoryAnvil legacy) {
      if (legacy.isRepairCostSet()) {
         this.setRepairCost(legacy.getRepairCost());
      }

      if (legacy.isRepairCostAmountSet()) {
         this.setRepairItemCountCost(legacy.getRepairCostAmount());
      }

      if (legacy.isMaximumRepairCostSet()) {
         this.setMaximumRepairCost(legacy.getMaximumRepairCost());
      }

   }
}
