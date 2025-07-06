package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.world.inventory.BrewingStandMenu;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.view.BrewingStandView;

public class CraftBrewingStandView extends CraftInventoryView<BrewingStandMenu, BrewerInventory> implements BrewingStandView {
   public CraftBrewingStandView(HumanEntity player, BrewerInventory viewing, BrewingStandMenu container) {
      super(player, viewing, container);
   }

   public int getFuelLevel() {
      return ((BrewingStandMenu)this.container).getFuel();
   }

   public int getBrewingTicks() {
      return ((BrewingStandMenu)this.container).getBrewingTicks();
   }

   public void setFuelLevel(int fuelLevel) {
      Preconditions.checkArgument(fuelLevel > 0, "The given fuel level must be greater than 0");
      ((BrewingStandMenu)this.container).setData(1, fuelLevel);
   }

   public void setBrewingTicks(int brewingTicks) {
      Preconditions.checkArgument(brewingTicks > 0, "The given brewing ticks must be greater than 0");
      ((BrewingStandMenu)this.container).setData(0, brewingTicks);
   }
}
