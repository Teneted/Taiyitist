package org.bukkit.craftbukkit.v1_21_R5.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

public class CraftContainer extends AbstractContainerMenu {
   private final InventoryView view;
   private InventoryType cachedType;
   private AbstractContainerMenu delegate;

   public CraftContainer(InventoryView view, Player player, int id) {
      super(getNotchInventoryType(view.getTopInventory()), id);
      this.view = view;
      Container top = ((CraftInventory)view.getTopInventory()).getInventory();
      Inventory bottom = (Inventory)((CraftInventory)view.getBottomInventory()).getInventory();
      this.cachedType = view.getType();
      this.setupSlots(top, bottom, player);
   }

   public CraftContainer(final org.bukkit.inventory.Inventory inventory, final Player player, int id) {
      this((InventoryView)(new CraftAbstractInventoryView() {
         private final String originalTitle = inventory instanceof CraftInventoryCustom ? ((CraftInventoryCustom.MinecraftInventory)((CraftInventory)inventory).getInventory()).getTitle() : inventory.getType().getDefaultTitle();
         private String title;

         {
            this.title = this.originalTitle;
         }

         public org.bukkit.inventory.Inventory getTopInventory() {
            return inventory;
         }

         public org.bukkit.inventory.Inventory getBottomInventory() {
            return this.getPlayer().getInventory();
         }

         public HumanEntity getPlayer() {
            return player.getBukkitEntity();
         }

         public InventoryType getType() {
            return inventory.getType();
         }

         public String getTitle() {
            return this.title;
         }

         public String getOriginalTitle() {
            return this.originalTitle;
         }

         public void setTitle(String title) {
            CraftInventoryView.sendInventoryTitleChange(this, title);
            this.title = title;
         }
      }), player, id);
   }

   public InventoryView getBukkitView() {
      return this.view;
   }

   public static MenuType getNotchInventoryType(org.bukkit.inventory.Inventory inventory) {
      InventoryType type = inventory.getType();
      switch (type) {
         case PLAYER:
         case CHEST:
         case ENDER_CHEST:
         case BARREL:
            switch (inventory.getSize()) {
               case 9:
                  return MenuType.GENERIC_9x1;
               case 18:
                  return MenuType.GENERIC_9x2;
               case 27:
                  return MenuType.GENERIC_9x3;
               case 36:
               case 41:
                  return MenuType.GENERIC_9x4;
               case 45:
                  return MenuType.GENERIC_9x5;
               case 54:
                  return MenuType.GENERIC_9x6;
               default:
                  throw new IllegalArgumentException("Unsupported custom inventory size " + inventory.getSize());
            }
         default:
            org.bukkit.inventory.MenuType menu = type.getMenuType();
            return menu == null ? MenuType.GENERIC_9x3 : (MenuType)((CraftMenuType)menu).getHandle();
      }
   }

   private void setupSlots(Container top, Inventory bottom, Player entityhuman) {
      int windowId = -1;
      switch (this.cachedType) {
         case PLAYER:
         case CHEST:
         case ENDER_CHEST:
         case BARREL:
            this.delegate = new ChestMenu(MenuType.GENERIC_9x3, windowId, bottom, top, top.getContainerSize() / 9);
         case CREATIVE:
         default:
            break;
         case DISPENSER:
         case DROPPER:
            this.delegate = new DispenserMenu(windowId, bottom, top);
            break;
         case FURNACE:
            this.delegate = new FurnaceMenu(windowId, bottom, top, new SimpleContainerData(4));
            break;
         case CRAFTING:
         case WORKBENCH:
            this.setupWorkbench(top, bottom);
            break;
         case ENCHANTING:
            this.delegate = new EnchantmentMenu(windowId, bottom);
            break;
         case BREWING:
            this.delegate = new BrewingStandMenu(windowId, bottom, top, new SimpleContainerData(2));
            break;
         case HOPPER:
            this.delegate = new HopperMenu(windowId, bottom, top);
            break;
         case ANVIL:
            this.setupAnvil(top, bottom);
            break;
         case BEACON:
            this.delegate = new BeaconMenu(windowId, bottom);
            break;
         case SHULKER_BOX:
            this.delegate = new ShulkerBoxMenu(windowId, bottom, top);
            break;
         case BLAST_FURNACE:
            this.delegate = new BlastFurnaceMenu(windowId, bottom, top, new SimpleContainerData(4));
            break;
         case LECTERN:
            this.delegate = new LecternMenu(windowId, top, new SimpleContainerData(1), bottom);
            break;
         case SMOKER:
            this.delegate = new SmokerMenu(windowId, bottom, top, new SimpleContainerData(4));
            break;
         case LOOM:
            this.delegate = new LoomMenu(windowId, bottom);
            break;
         case CARTOGRAPHY:
            this.delegate = new CartographyTableMenu(windowId, bottom);
            break;
         case GRINDSTONE:
            this.delegate = new GrindstoneMenu(windowId, bottom);
            break;
         case STONECUTTER:
            this.setupStoneCutter(top, bottom);
            break;
         case MERCHANT:
            this.delegate = new MerchantMenu(windowId, bottom);
            break;
         case SMITHING:
         case SMITHING_NEW:
            this.setupSmithing(top, bottom);
            break;
         case CRAFTER:
            this.delegate = new CrafterMenu(windowId, bottom);
      }

      if (this.delegate != null) {
         this.lastSlots = this.delegate.lastSlots;
         this.slots = this.delegate.slots;
         this.remoteSlots = this.delegate.remoteSlots;
      }

      switch (this.cachedType) {
         case WORKBENCH -> this.delegate = new CraftingMenu(windowId, bottom);
         case ANVIL -> this.delegate = new AnvilMenu(windowId, bottom);
      }

   }

   private void setupWorkbench(Container top, Container bottom) {
      this.addSlot(new Slot(top, 0, 124, 35));

      int row;
      int col;
      for(row = 0; row < 3; ++row) {
         for(col = 0; col < 3; ++col) {
            this.addSlot(new Slot(top, 1 + col + row * 3, 30 + col * 18, 17 + row * 18));
         }
      }

      for(row = 0; row < 3; ++row) {
         for(col = 0; col < 9; ++col) {
            this.addSlot(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for(col = 0; col < 9; ++col) {
         this.addSlot(new Slot(bottom, col, 8 + col * 18, 142));
      }

   }

   private void setupAnvil(Container top, Container bottom) {
      this.addSlot(new Slot(top, 0, 27, 47));
      this.addSlot(new Slot(top, 1, 76, 47));
      this.addSlot(new Slot(top, 2, 134, 47));

      int row;
      for(row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for(row = 0; row < 9; ++row) {
         this.addSlot(new Slot(bottom, row, 8 + row * 18, 142));
      }

   }

   private void setupSmithing(Container top, Container bottom) {
      this.addSlot(new Slot(top, 0, 8, 48));
      this.addSlot(new Slot(top, 1, 26, 48));
      this.addSlot(new Slot(top, 2, 44, 48));
      this.addSlot(new Slot(top, 3, 98, 48));

      int row;
      for(row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for(row = 0; row < 9; ++row) {
         this.addSlot(new Slot(bottom, row, 8 + row * 18, 142));
      }

   }

   private void setupStoneCutter(Container top, Container bottom) {
      this.addSlot(new Slot(top, 0, 20, 33));
      this.addSlot(new Slot(top, 1, 143, 33));

      int row;
      for(row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for(row = 0; row < 9; ++row) {
         this.addSlot(new Slot(bottom, row, 8 + row * 18, 142));
      }

   }

   public ItemStack quickMoveStack(Player entityhuman, int i) {
      return this.delegate != null ? this.delegate.quickMoveStack(entityhuman, i) : ItemStack.EMPTY;
   }

   public boolean stillValid(Player entity) {
      return true;
   }

   public MenuType<?> getType() {
      return getNotchInventoryType(this.view.getTopInventory());
   }
}
