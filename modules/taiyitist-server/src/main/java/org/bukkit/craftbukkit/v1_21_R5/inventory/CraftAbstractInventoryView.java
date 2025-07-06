package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftHumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CraftAbstractInventoryView implements InventoryView {
   public void setItem(int slot, @Nullable ItemStack item) {
      Inventory inventory = this.getInventory(slot);
      if (inventory != null) {
         inventory.setItem(this.convertSlot(slot), item);
      } else if (item != null) {
         Player handle = ((CraftHumanEntity)this.getPlayer()).getHandle();
         Containers.dropItemStack(handle.level(), handle.getX(), handle.getY(), handle.getZ(), CraftItemStack.asNMSCopy(item));
      }

   }

   @Nullable
   public ItemStack getItem(int slot) {
      Inventory inventory = this.getInventory(slot);
      return inventory == null ? null : inventory.getItem(this.convertSlot(slot));
   }

   public void setCursor(@Nullable ItemStack item) {
      this.getPlayer().setItemOnCursor(item);
   }

   @Nullable
   public ItemStack getCursor() {
      return this.getPlayer().getItemOnCursor();
   }

   @Nullable
   public Inventory getInventory(int rawSlot) {
      if (rawSlot != -999 && rawSlot != -1) {
         Preconditions.checkArgument(rawSlot >= 0, "Negative, non outside slot %s", rawSlot);
         Preconditions.checkArgument(rawSlot < this.countSlots(), "Slot %s greater than inventory slot count", rawSlot);
         return rawSlot < this.getTopInventory().getSize() ? this.getTopInventory() : this.getBottomInventory();
      } else {
         return null;
      }
   }

   public int convertSlot(int rawSlot) {
      int numInTop = this.getTopInventory().getSize();
      if (rawSlot < numInTop) {
         return rawSlot;
      } else {
         int slot = rawSlot - numInTop;
         if (this.getType() == InventoryType.CRAFTING || this.getType() == InventoryType.CREATIVE) {
            if (slot < 4) {
               return 39 - slot;
            }

            if (slot > 39) {
               return slot;
            }

            slot -= 4;
         }

         if (slot >= 27) {
            slot -= 27;
         } else {
            slot += 9;
         }

         return slot;
      }
   }

   @NotNull
   public InventoryType.SlotType getSlotType(int slot) {
      InventoryType.SlotType type = SlotType.CONTAINER;
      if (slot >= 0 && slot < this.getTopInventory().getSize()) {
         switch (this.getType()) {
            case BLAST_FURNACE:
            case FURNACE:
            case SMOKER:
               if (slot == 2) {
                  type = SlotType.RESULT;
               } else if (slot == 1) {
                  type = SlotType.FUEL;
               } else {
                  type = SlotType.CRAFTING;
               }
               break;
            case BREWING:
               if (slot == 3) {
                  type = SlotType.FUEL;
               } else {
                  type = SlotType.CRAFTING;
               }
               break;
            case ENCHANTING:
               type = SlotType.CRAFTING;
               break;
            case WORKBENCH:
            case CRAFTING:
               if (slot == 0) {
                  type = SlotType.RESULT;
               } else {
                  type = SlotType.CRAFTING;
               }
               break;
            case BEACON:
               type = SlotType.CRAFTING;
               break;
            case ANVIL:
            case SMITHING:
            case CARTOGRAPHY:
            case GRINDSTONE:
            case MERCHANT:
               if (slot == 2) {
                  type = SlotType.RESULT;
               } else {
                  type = SlotType.CRAFTING;
               }
               break;
            case STONECUTTER:
               if (slot == 1) {
                  type = SlotType.RESULT;
               } else {
                  type = SlotType.CRAFTING;
               }
               break;
            case LOOM:
            case SMITHING_NEW:
               if (slot == 3) {
                  type = SlotType.RESULT;
               } else {
                  type = SlotType.CRAFTING;
               }
         }
      } else if (slot < 0) {
         type = SlotType.OUTSIDE;
      } else if (this.getType() == InventoryType.CRAFTING) {
         if (slot < 9) {
            type = SlotType.ARMOR;
         } else if (slot > 35) {
            type = SlotType.QUICKBAR;
         }
      } else if (slot >= this.countSlots() - 14) {
         type = SlotType.QUICKBAR;
      }

      return type;
   }

   public void close() {
      this.getPlayer().closeInventory();
   }

   public int countSlots() {
      return this.getTopInventory().getSize() + this.getBottomInventory().getSize();
   }

   public boolean setProperty(@NotNull InventoryView.Property prop, int value) {
      return this.getPlayer().setWindowProperty(prop, value);
   }
}
