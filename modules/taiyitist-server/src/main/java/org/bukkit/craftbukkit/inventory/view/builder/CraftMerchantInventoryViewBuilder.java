package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.craftbukkit.inventory.CraftMerchantCustom;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder;

public class CraftMerchantInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements MerchantInventoryViewBuilder<V> {
   private Merchant merchant;

   public CraftMerchantInventoryViewBuilder(MenuType<?> handle) {
      super(handle);
   }

   public MerchantInventoryViewBuilder<V> title(String title) {
      return (MerchantInventoryViewBuilder)super.title(title);
   }

   public MerchantInventoryViewBuilder<V> merchant(org.bukkit.inventory.Merchant merchant) {
      this.merchant = ((CraftMerchant)merchant).getMerchant();
      return this;
   }

   public MerchantInventoryViewBuilder<V> checkReachable(boolean checkReachable) {
      super.checkReachable = checkReachable;
      return this;
   }

   public V build(HumanEntity player) {
      Preconditions.checkArgument(player != null, "The given player must not be null");
      Preconditions.checkArgument(this.title != null, "The given title must not be null");
      Preconditions.checkArgument(player instanceof CraftHumanEntity, "The given player must be a CraftHumanEntity");
      CraftHumanEntity craftHuman = (CraftHumanEntity)player;
      Preconditions.checkArgument(craftHuman.getHandle() instanceof ServerPlayer, "The given player must be an EntityPlayer");
      ServerPlayer serverPlayer = (ServerPlayer)craftHuman.getHandle();
      MerchantMenu container;
      if (this.merchant == null) {
         container = new MerchantMenu(serverPlayer.nextContainerCounterInt(), serverPlayer.getInventory(), (new CraftMerchantCustom(this.title)).getMerchant());
      } else {
         container = new MerchantMenu(serverPlayer.nextContainerCounterInt(), serverPlayer.getInventory(), this.merchant);
      }

      container.taiyitist$setCheckReachable(super.checkReachable);
      container.setTitle(CraftChatMessage.fromString(this.title)[0]);
      return (V) container.getBukkitView();
   }

   protected AbstractContainerMenu buildContainer(ServerPlayer player) {
      throw new UnsupportedOperationException("buildContainer is not supported for CraftMerchantInventoryViewBuilder");
   }

   public MerchantInventoryViewBuilder<V> copy() {
      CraftMerchantInventoryViewBuilder<V> copy = new CraftMerchantInventoryViewBuilder(super.handle);
      copy.checkReachable = super.checkReachable;
      copy.merchant = this.merchant;
      copy.title = this.title;
      return copy;
   }
}
