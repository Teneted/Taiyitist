package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.view.MerchantView;
import org.jetbrains.annotations.NotNull;

public class CraftMerchantView extends CraftInventoryView<MerchantMenu, MerchantInventory> implements MerchantView {
   private final Merchant trader;

   public CraftMerchantView(HumanEntity player, MerchantInventory viewing, MerchantMenu container, Merchant trader) {
      super(player, viewing, container);
      this.trader = trader;
   }

   @NotNull
   public org.bukkit.inventory.Merchant getMerchant() {
      return this.trader.getCraftMerchant();
   }
}
