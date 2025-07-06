package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLegacy;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class CraftInventory implements Inventory {
   protected final Container inventory;

   public CraftInventory(Container inventory) {
      this.inventory = inventory;
   }

   public Container getInventory() {
      return this.inventory;
   }

   public int getSize() {
      return this.getInventory().getContainerSize();
   }

   public ItemStack getItem(int index) {
      net.minecraft.world.item.ItemStack item = this.getInventory().getItem(index);
      return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
   }

   protected ItemStack[] asCraftMirror(List<net.minecraft.world.item.ItemStack> mcItems) {
      int size = mcItems.size();
      ItemStack[] items = new ItemStack[size];

      for(int i = 0; i < size; ++i) {
         net.minecraft.world.item.ItemStack mcItem = (net.minecraft.world.item.ItemStack)mcItems.get(i);
         items[i] = mcItem.isEmpty() ? null : CraftItemStack.asCraftMirror(mcItem);
      }

      return items;
   }

   public ItemStack[] getStorageContents() {
      return this.getContents();
   }

   public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
      this.setContents(items);
   }

   public ItemStack[] getContents() {
      List<net.minecraft.world.item.ItemStack> mcItems = this.getInventory().getContents();
      return this.asCraftMirror(mcItems);
   }

   public void setContents(ItemStack[] items) {
      Preconditions.checkArgument(items.length <= this.getSize(), "Invalid inventory size (%s); expected %s or less", items.length, this.getSize());

      for(int i = 0; i < this.getSize(); ++i) {
         if (i >= items.length) {
            this.setItem(i, (ItemStack)null);
         } else {
            this.setItem(i, items[i]);
         }
      }

   }

   public void setItem(int index, ItemStack item) {
      this.getInventory().setItem(index, CraftItemStack.asNMSCopy(item));
   }

   public boolean contains(Material material) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      material = CraftLegacy.fromLegacy(material);
      ItemStack[] var2 = this.getStorageContents();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack item = var2[var4];
         if (item != null && item.getType() == material) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(ItemStack item) {
      if (item == null) {
         return false;
      } else {
         ItemStack[] var2 = this.getStorageContents();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack i = var2[var4];
            if (item.equals(i)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean contains(Material material, int amount) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      material = CraftLegacy.fromLegacy(material);
      if (amount <= 0) {
         return true;
      } else {
         ItemStack[] var3 = this.getStorageContents();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack item = var3[var5];
            if (item != null && item.getType() == material && (amount -= item.getAmount()) <= 0) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean contains(ItemStack item, int amount) {
      if (item == null) {
         return false;
      } else if (amount <= 0) {
         return true;
      } else {
         ItemStack[] var3 = this.getStorageContents();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack i = var3[var5];
            if (item.equals(i)) {
               --amount;
               if (amount <= 0) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean containsAtLeast(ItemStack item, int amount) {
      if (item == null) {
         return false;
      } else if (amount <= 0) {
         return true;
      } else {
         ItemStack[] var3 = this.getStorageContents();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack i = var3[var5];
            if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
               return true;
            }
         }

         return false;
      }
   }

   public HashMap<Integer, ItemStack> all(Material material) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      material = CraftLegacy.fromLegacy(material);
      HashMap<Integer, ItemStack> slots = new HashMap();
      ItemStack[] inventory = this.getStorageContents();

      for(int i = 0; i < inventory.length; ++i) {
         ItemStack item = inventory[i];
         if (item != null && item.getType() == material) {
            slots.put(i, item);
         }
      }

      return slots;
   }

   public HashMap<Integer, ItemStack> all(ItemStack item) {
      HashMap<Integer, ItemStack> slots = new HashMap();
      if (item != null) {
         ItemStack[] inventory = this.getStorageContents();

         for(int i = 0; i < inventory.length; ++i) {
            if (item.equals(inventory[i])) {
               slots.put(i, inventory[i]);
            }
         }
      }

      return slots;
   }

   public int first(Material material) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      material = CraftLegacy.fromLegacy(material);
      ItemStack[] inventory = this.getStorageContents();

      for(int i = 0; i < inventory.length; ++i) {
         ItemStack item = inventory[i];
         if (item != null && item.getType() == material) {
            return i;
         }
      }

      return -1;
   }

   public int first(ItemStack item) {
      return this.first(item, true);
   }

   private int first(ItemStack item, boolean withAmount) {
      if (item == null) {
         return -1;
      } else {
         ItemStack[] inventory = this.getStorageContents();
         int i = 0;

         while(true) {
            if (i >= inventory.length) {
               return -1;
            }

            if (inventory[i] != null) {
               if (withAmount) {
                  if (item.equals(inventory[i])) {
                     break;
                  }
               } else if (item.isSimilar(inventory[i])) {
                  break;
               }
            }

            ++i;
         }

         return i;
      }
   }

   public int firstEmpty() {
      ItemStack[] inventory = this.getStorageContents();

      for(int i = 0; i < inventory.length; ++i) {
         if (inventory[i] == null) {
            return i;
         }
      }

      return -1;
   }

   public boolean isEmpty() {
      return this.inventory.isEmpty();
   }

   private int firstPartial(ItemStack item) {
      ItemStack[] inventory = this.getStorageContents();
      ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
      if (item == null) {
         return -1;
      } else {
         for(int i = 0; i < inventory.length; ++i) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < this.getMaxItemStack(cItem) && cItem.isSimilar(filteredItem)) {
               return i;
            }
         }

         return -1;
      }
   }

   public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
      Preconditions.checkArgument(items != null, "items cannot be null");
      HashMap<Integer, ItemStack> leftover = new HashMap();

      label49:
      for(int i = 0; i < items.length; ++i) {
         ItemStack item = items[i];
         Preconditions.checkArgument(item != null, "ItemStack cannot be null");
         if (!item.getType().isAir()) {
            while(true) {
               while(true) {
                  int firstPartial = this.firstPartial(item);
                  int maxAmount;
                  if (firstPartial == -1) {
                     int firstFree = this.firstEmpty();
                     if (firstFree == -1) {
                        leftover.put(i, item);
                        continue label49;
                     }

                     maxAmount = this.getMaxItemStack(item);
                     if (item.getAmount() <= maxAmount) {
                        this.setItem(firstFree, item);
                        continue label49;
                     }

                     CraftItemStack stack = CraftItemStack.asCraftCopy(item);
                     stack.setAmount(maxAmount);
                     this.setItem(firstFree, stack);
                     item.setAmount(item.getAmount() - maxAmount);
                  } else {
                     ItemStack partialItem = this.getItem(firstPartial);
                     maxAmount = item.getAmount();
                     int partialAmount = partialItem.getAmount();
                     int maxAmount = this.getMaxItemStack(partialItem);
                     if (maxAmount + partialAmount <= maxAmount) {
                        partialItem.setAmount(maxAmount + partialAmount);
                        this.setItem(firstPartial, partialItem);
                        continue label49;
                     }

                     partialItem.setAmount(maxAmount);
                     this.setItem(firstPartial, partialItem);
                     item.setAmount(maxAmount + partialAmount - maxAmount);
                  }
               }
            }
         }
      }

      return leftover;
   }

   public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
      Preconditions.checkArgument(items != null, "items cannot be null");
      HashMap<Integer, ItemStack> leftover = new HashMap();

      for(int i = 0; i < items.length; ++i) {
         ItemStack item = items[i];
         Preconditions.checkArgument(item != null, "ItemStack cannot be null");
         if (!item.getType().isAir()) {
            int toDelete = item.getAmount();

            while(true) {
               int first = this.first(item, false);
               if (first == -1) {
                  item.setAmount(toDelete);
                  leftover.put(i, item);
                  break;
               }

               ItemStack itemStack = this.getItem(first);
               int amount = itemStack.getAmount();
               if (amount <= toDelete) {
                  toDelete -= amount;
                  this.clear(first);
               } else {
                  itemStack.setAmount(amount - toDelete);
                  this.setItem(first, itemStack);
                  toDelete = 0;
               }

               if (toDelete <= 0) {
                  break;
               }
            }
         }
      }

      return leftover;
   }

   private int getMaxItemStack(ItemStack itemstack) {
      return Math.min(itemstack.getMaxStackSize(), this.getInventory().getMaxStackSize());
   }

   public void remove(Material material) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      material = CraftLegacy.fromLegacy(material);
      ItemStack[] items = this.getStorageContents();

      for(int i = 0; i < items.length; ++i) {
         if (items[i] != null && items[i].getType() == material) {
            this.clear(i);
         }
      }

   }

   public void remove(ItemStack item) {
      ItemStack[] items = this.getStorageContents();

      for(int i = 0; i < items.length; ++i) {
         if (items[i] != null && items[i].equals(item)) {
            this.clear(i);
         }
      }

   }

   public void clear(int index) {
      this.setItem(index, (ItemStack)null);
   }

   public void clear() {
      for(int i = 0; i < this.getSize(); ++i) {
         this.clear(i);
      }

   }

   public ListIterator<ItemStack> iterator() {
      return new InventoryIterator(this);
   }

   public ListIterator<ItemStack> iterator(int index) {
      if (index < 0) {
         index += this.getSize() + 1;
      }

      return new InventoryIterator(this, index);
   }

   public List<HumanEntity> getViewers() {
      return this.inventory.getViewers();
   }

   public InventoryType getType() {
      if (this.inventory instanceof CraftingContainer) {
         if (this.inventory instanceof CrafterBlockEntity) {
            return InventoryType.CRAFTER;
         } else {
            return this.inventory.getContainerSize() >= 9 ? InventoryType.WORKBENCH : InventoryType.CRAFTING;
         }
      } else if (this.inventory instanceof net.minecraft.world.entity.player.Inventory) {
         return InventoryType.PLAYER;
      } else if (this.inventory instanceof DropperBlockEntity) {
         return InventoryType.DROPPER;
      } else if (this.inventory instanceof DispenserBlockEntity) {
         return InventoryType.DISPENSER;
      } else if (this.inventory instanceof BlastFurnaceBlockEntity) {
         return InventoryType.BLAST_FURNACE;
      } else if (this.inventory instanceof SmokerBlockEntity) {
         return InventoryType.SMOKER;
      } else if (this.inventory instanceof AbstractFurnaceBlockEntity) {
         return InventoryType.FURNACE;
      } else if (this instanceof CraftInventoryEnchanting) {
         return InventoryType.ENCHANTING;
      } else if (this.inventory instanceof BrewingStandBlockEntity) {
         return InventoryType.BREWING;
      } else if (this.inventory instanceof CraftInventoryCustom.MinecraftInventory) {
         return ((CraftInventoryCustom.MinecraftInventory)this.inventory).getType();
      } else if (this.inventory instanceof PlayerEnderChestContainer) {
         return InventoryType.ENDER_CHEST;
      } else if (this.inventory instanceof MerchantContainer) {
         return InventoryType.MERCHANT;
      } else if (this instanceof CraftInventoryBeacon) {
         return InventoryType.BEACON;
      } else if (this instanceof CraftInventoryAnvil) {
         return InventoryType.ANVIL;
      } else if (this instanceof CraftInventorySmithing) {
         return InventoryType.SMITHING;
      } else if (this.inventory instanceof Hopper) {
         return InventoryType.HOPPER;
      } else if (this.inventory instanceof ShulkerBoxBlockEntity) {
         return InventoryType.SHULKER_BOX;
      } else if (this.inventory instanceof BarrelBlockEntity) {
         return InventoryType.BARREL;
      } else if (this.inventory instanceof LecternBlockEntity.LecternInventory) {
         return InventoryType.LECTERN;
      } else if (this.inventory instanceof ChiseledBookShelfBlockEntity) {
         return InventoryType.CHISELED_BOOKSHELF;
      } else if (this instanceof CraftInventoryLoom) {
         return InventoryType.LOOM;
      } else if (this instanceof CraftInventoryCartography) {
         return InventoryType.CARTOGRAPHY;
      } else if (this instanceof CraftInventoryGrindstone) {
         return InventoryType.GRINDSTONE;
      } else if (this instanceof CraftInventoryStonecutter) {
         return InventoryType.STONECUTTER;
      } else if (!(this.inventory instanceof ComposterBlock.EmptyContainer) && !(this.inventory instanceof ComposterBlock.InputContainer) && !(this.inventory instanceof ComposterBlock.OutputContainer)) {
         if (this.inventory instanceof JukeboxBlockEntity) {
            return InventoryType.JUKEBOX;
         } else {
            return this.inventory instanceof DecoratedPotBlockEntity ? InventoryType.DECORATED_POT : InventoryType.CHEST;
         }
      } else {
         return InventoryType.COMPOSTER;
      }
   }

   public InventoryHolder getHolder() {
      return this.inventory.getOwner();
   }

   public int getMaxStackSize() {
      return this.inventory.getMaxStackSize();
   }

   public void setMaxStackSize(int size) {
      this.inventory.setMaxStackSize(size);
   }

   public int hashCode() {
      return this.inventory.hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof CraftInventory && ((CraftInventory)obj).inventory.equals(this.inventory);
   }

   public Location getLocation() {
      return this.inventory.getLocation();
   }
}
