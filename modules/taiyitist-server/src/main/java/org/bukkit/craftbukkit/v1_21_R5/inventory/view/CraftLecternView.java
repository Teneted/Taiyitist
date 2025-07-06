package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.world.inventory.LecternMenu;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.LecternInventory;
import org.bukkit.inventory.view.LecternView;

public class CraftLecternView extends CraftInventoryView<LecternMenu, LecternInventory> implements LecternView {
   public CraftLecternView(HumanEntity player, LecternInventory viewing, LecternMenu container) {
      super(player, viewing, container);
   }

   public int getPage() {
      return ((LecternMenu)this.container).getPage();
   }

   public void setPage(int page) {
      Preconditions.checkArgument(page >= 0, "The minimum page is 0");
      ((LecternMenu)this.container).setData(0, page);
   }
}
