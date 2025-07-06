package org.bukkit.craftbukkit.inventory.view.builder;

import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

public class CraftDoubleChestInventoryViewBuilder<V extends InventoryView> extends CraftAbstractLocationInventoryViewBuilder<V> {
   public CraftDoubleChestInventoryViewBuilder(MenuType<?> handle) {
      super(handle);
   }

   protected AbstractContainerMenu buildContainer(ServerPlayer player) {
      if (super.world == null) {
         return this.handle.create(player.nextContainerCounterInt(), player.getInventory());
      } else {
         ChestBlock chest = (ChestBlock)Blocks.CHEST;
         DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> result = chest.combine(super.world.getBlockState(super.position), super.world, super.position, false);
         if (result instanceof DoubleBlockCombiner.NeighborCombineResult.Single) {
            return this.handle.create(player.nextContainerCounterInt(), player.getInventory());
         } else {
            MenuProvider combined = (MenuProvider)((Optional)result.apply(ChestBlock.MENU_PROVIDER_COMBINER)).orElse((Object)null);
            return combined == null ? this.handle.create(player.nextContainerCounterInt(), player.getInventory()) : combined.createMenu(player.nextContainerCounterInt(), player.getInventory(), player);
         }
      }
   }

   public LocationInventoryViewBuilder<V> copy() {
      CraftDoubleChestInventoryViewBuilder<V> copy = new CraftDoubleChestInventoryViewBuilder(super.handle);
      copy.world = this.world;
      copy.position = this.position;
      copy.checkReachable = super.checkReachable;
      copy.title = this.title;
      return copy;
   }
}
