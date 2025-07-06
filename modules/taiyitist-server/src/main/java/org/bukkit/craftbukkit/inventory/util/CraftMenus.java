package org.bukkit.craftbukkit.inventory.util;

import java.util.function.Supplier;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.craftbukkit.inventory.view.builder.CraftAccessLocationInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftBlockEntityInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftDoubleChestInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftMerchantInventoryViewBuilder;
import org.bukkit.craftbukkit.inventory.view.builder.CraftStandardInventoryViewBuilder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.inventory.view.BeaconView;
import org.bukkit.inventory.view.BrewingStandView;
import org.bukkit.inventory.view.CrafterView;
import org.bukkit.inventory.view.EnchantmentView;
import org.bukkit.inventory.view.FurnaceView;
import org.bukkit.inventory.view.LecternView;
import org.bukkit.inventory.view.LoomView;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.StonecutterView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

public final class CraftMenus {
   public static void openMerchantMenu(ServerPlayer player, MerchantMenu merchant) {
      Merchant minecraftMerchant = ((CraftMerchant)merchant.getBukkitView().getMerchant()).getMerchant();
      int level = 1;
      if (minecraftMerchant instanceof Villager villager) {
         level = villager.getVillagerData().level();
      }

      if (minecraftMerchant.getTradingPlayer() != null) {
         minecraftMerchant.getTradingPlayer().closeContainer();
      }

      minecraftMerchant.setTradingPlayer(player);
      player.connection.send(new ClientboundOpenScreenPacket(merchant.containerId, MenuType.MERCHANT, merchant.getTitle()));
      player.containerMenu = merchant;
      player.initMenu(merchant);
      MerchantOffers merchantrecipelist = minecraftMerchant.getOffers();
      if (!merchantrecipelist.isEmpty()) {
         player.sendMerchantOffers(merchant.containerId, merchantrecipelist, level, minecraftMerchant.getVillagerXp(), minecraftMerchant.showProgressBar(), minecraftMerchant.canRestock());
      }

   }

   public static <V extends InventoryView, B extends InventoryViewBuilder<V>> MenuTypeData<V, B> getMenuTypeData(CraftMenuType<?, ?> menuType) {
      MenuType<?> handle = (MenuType)menuType.getHandle();
      if (menuType == org.bukkit.inventory.MenuType.GENERIC_9X6) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftDoubleChestInventoryViewBuilder(handle);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.GENERIC_9X3) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.CHEST, (CraftBlockEntityInventoryViewBuilder.CraftTileInventoryBuilder)null);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.GENERIC_3X3) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.DISPENSER, DispenserBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.CRAFTER_3X3) {
         return asType(new MenuTypeData(CrafterView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.CRAFTER, CrafterBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.ANVIL) {
         return asType(new MenuTypeData(AnvilView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, AnvilMenu::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.BEACON) {
         return asType(new MenuTypeData(BeaconView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.BEACON, BeaconBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.BLAST_FURNACE) {
         return asType(new MenuTypeData(FurnaceView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.BLAST_FURNACE, BlastFurnaceBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.BREWING_STAND) {
         return asType(new MenuTypeData(BrewingStandView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.BREWING_STAND, BrewingStandBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.CRAFTING) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, CraftingMenu::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.ENCHANTMENT) {
         return asType(new MenuTypeData(EnchantmentView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, EnchantmentMenu::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.FURNACE) {
         return asType(new MenuTypeData(FurnaceView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.FURNACE, FurnaceBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.GRINDSTONE) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, GrindstoneMenu::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.HOPPER) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.HOPPER, HopperBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.LECTERN) {
         return asType(new MenuTypeData(LecternView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.LECTERN, LecternBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.LOOM) {
         return asType(new MenuTypeData(LoomView.class, () -> {
            return new CraftStandardInventoryViewBuilder(handle);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.MERCHANT) {
         return asType(new MenuTypeData(MerchantView.class, () -> {
            return new CraftMerchantInventoryViewBuilder(handle);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.SHULKER_BOX) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.SHULKER_BOX, (CraftBlockEntityInventoryViewBuilder.CraftTileInventoryBuilder)null);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.SMITHING) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, SmithingMenu::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.SMOKER) {
         return asType(new MenuTypeData(FurnaceView.class, () -> {
            return new CraftBlockEntityInventoryViewBuilder(handle, Blocks.SMOKER, SmokerBlockEntity::new);
         }));
      } else if (menuType == org.bukkit.inventory.MenuType.CARTOGRAPHY_TABLE) {
         return asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, CartographyTableMenu::new);
         }));
      } else {
         return menuType == org.bukkit.inventory.MenuType.STONECUTTER ? asType(new MenuTypeData(StonecutterView.class, () -> {
            return new CraftAccessLocationInventoryViewBuilder(handle, StonecutterMenu::new);
         })) : asType(new MenuTypeData(InventoryView.class, () -> {
            return new CraftStandardInventoryViewBuilder(handle);
         }));
      }
   }

   private static <V extends InventoryView, B extends InventoryViewBuilder<V>> MenuTypeData<V, B> asType(MenuTypeData<?, ?> data) {
      return data;
   }

   public static record MenuTypeData<V extends InventoryView, B extends InventoryViewBuilder<V>>(Class<V> viewClass, Supplier<B> viewBuilder) {
      public MenuTypeData(Class<V> viewClass, Supplier<B> viewBuilder) {
         this.viewClass = viewClass;
         this.viewBuilder = viewBuilder;
      }

      public Class<V> viewClass() {
         return this.viewClass;
      }

      public Supplier<B> viewBuilder() {
         return this.viewBuilder;
      }
   }
}
