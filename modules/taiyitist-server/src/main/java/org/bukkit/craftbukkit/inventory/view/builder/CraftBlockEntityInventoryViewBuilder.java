package org.bukkit.craftbukkit.inventory.view.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;

public class CraftBlockEntityInventoryViewBuilder<V extends InventoryView> extends CraftAbstractLocationInventoryViewBuilder<V> {
   private final Block block;
   private final CraftTileInventoryBuilder builder;

   public CraftBlockEntityInventoryViewBuilder(MenuType<?> handle, Block block, CraftTileInventoryBuilder builder) {
      super(handle);
      this.block = block;
      this.builder = builder;
   }

   protected AbstractContainerMenu buildContainer(ServerPlayer player) {
      if (this.world == null) {
         this.world = player.level();
      }

      if (this.position == null) {
         this.position = player.blockPosition();
      }

      BlockEntity entity = this.world.getBlockEntity(this.position);
      if (entity instanceof MenuConstructor container) {
         AbstractContainerMenu atBlock = container.createMenu(player.nextContainerCounter(), player.getInventory(), player);
         return atBlock.getType() != super.handle ? this.buildFakeTile(player) : atBlock;
      } else {
         return this.buildFakeTile(player);
      }
   }

   private AbstractContainerMenu buildFakeTile(ServerPlayer player) {
      if (this.builder == null) {
         return this.handle.create(player.nextContainerCounter(), player.getInventory());
      } else {
         MenuProvider inventory = this.builder.build(this.position, this.block.defaultBlockState());
         if (inventory instanceof BlockEntity) {
            BlockEntity tile = (BlockEntity)inventory;
            tile.setLevel(this.world);
         }

         return inventory.createMenu(player.nextContainerCounter(), player.getInventory(), player);
      }
   }

   public LocationInventoryViewBuilder<V> copy() {
      CraftBlockEntityInventoryViewBuilder<V> copy = new CraftBlockEntityInventoryViewBuilder(super.handle, this.block, this.builder);
      copy.world = this.world;
      copy.position = this.position;
      copy.checkReachable = super.checkReachable;
      copy.title = this.title;
      return copy;
   }

   public interface CraftTileInventoryBuilder {
      MenuProvider build(BlockPos var1, BlockState var2);
   }
}
