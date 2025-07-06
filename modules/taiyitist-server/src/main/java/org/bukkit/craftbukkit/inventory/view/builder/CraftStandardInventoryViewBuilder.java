package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

public class CraftStandardInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> {
   public CraftStandardInventoryViewBuilder(MenuType<?> handle) {
      super(handle);
   }

   protected AbstractContainerMenu buildContainer(ServerPlayer player) {
      return super.handle.create(player.nextContainerCounter(), player.getInventory());
   }

   public InventoryViewBuilder<V> copy() {
      CraftStandardInventoryViewBuilder<V> copy = new CraftStandardInventoryViewBuilder(this.handle);
      copy.title = this.title;
      return copy;
   }
}
