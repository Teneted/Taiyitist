package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.AbstractFurnaceMenu;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.view.FurnaceView;

public class CraftFurnaceView extends CraftInventoryView<AbstractFurnaceMenu, FurnaceInventory> implements FurnaceView {
   public CraftFurnaceView(HumanEntity player, FurnaceInventory viewing, AbstractFurnaceMenu container) {
      super(player, viewing, container);
   }

   public float getCookTime() {
      return ((AbstractFurnaceMenu)this.container).getBurnProgress();
   }

   public float getBurnTime() {
      return ((AbstractFurnaceMenu)this.container).getLitProgress();
   }

   public boolean isBurning() {
      return ((AbstractFurnaceMenu)this.container).isLit();
   }

   public void setCookTime(int cookProgress, int cookDuration) {
      ((AbstractFurnaceMenu)this.container).setData(2, cookProgress);
      ((AbstractFurnaceMenu)this.container).setData(3, cookDuration);
   }

   public void setBurnTime(int burnProgress, int burnDuration) {
      ((AbstractFurnaceMenu)this.container).setData(0, burnProgress);
      ((AbstractFurnaceMenu)this.container).setData(1, burnDuration);
   }
}
