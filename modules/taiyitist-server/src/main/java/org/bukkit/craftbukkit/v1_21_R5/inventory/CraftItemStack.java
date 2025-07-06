package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_21_R5.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLegacy;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.ApiStatus.Internal;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {
   net.minecraft.world.item.ItemStack handle;
   private boolean isForInventoryDrop;

   public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original) {
      if (original instanceof CraftItemStack stack) {
         return stack.handle == null ? net.minecraft.world.item.ItemStack.EMPTY : stack.handle.copy();
      } else if (original != null && original.getType() != Material.AIR) {
         Item item = CraftItemType.bukkitToMinecraft(original.getType());
         if (item == null) {
            return net.minecraft.world.item.ItemStack.EMPTY;
         } else {
            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, original.getAmount());
            if (original.hasItemMeta()) {
               setItemMeta(stack, original.getItemMeta());
            }

            return stack;
         }
      } else {
         return net.minecraft.world.item.ItemStack.EMPTY;
      }
   }

   public static net.minecraft.world.item.ItemStack copyNMSStack(net.minecraft.world.item.ItemStack original, int amount) {
      net.minecraft.world.item.ItemStack stack = original.copy();
      stack.setCount(amount);
      return stack;
   }

   public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original) {
      if (original.isEmpty()) {
         return new ItemStack(Material.AIR);
      } else {
         ItemStack stack = new ItemStack(CraftItemType.minecraftToBukkit(original.getItem()), original.getCount());
         if (hasItemMeta(original)) {
            stack.setItemMeta(getItemMeta(original));
         }

         return stack;
      }
   }

   public static CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack original) {
      return new CraftItemStack(original != null && !original.isEmpty() ? original : null);
   }

   public static CraftItemStack asCraftCopy(ItemStack original) {
      if (original instanceof CraftItemStack stack) {
         return new CraftItemStack(stack.handle == null ? null : stack.handle.copy());
      } else {
         return new CraftItemStack(original);
      }
   }

   public static CraftItemStack asNewCraftStack(Item item) {
      return asNewCraftStack(item, 1);
   }

   public static CraftItemStack asNewCraftStack(Item item, int amount) {
      return new CraftItemStack(CraftItemType.minecraftToBukkit(item), amount, (short)0, (ItemMeta)null);
   }

   public static ItemPredicate asCriterionConditionItem(ItemStack original) {
      net.minecraft.world.item.ItemStack nms = asNMSCopy(original);
      DataComponentExactPredicate predicate = DataComponentExactPredicate.allOf(PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, nms.getComponentsPatch()));
      return new ItemPredicate(Optional.of(HolderSet.direct(new Holder[]{nms.getItemHolder()})), Ints.ANY, new DataComponentMatchers(predicate, Collections.emptyMap()));
   }

   private CraftItemStack(net.minecraft.world.item.ItemStack item) {
      this.handle = item;
   }

   private CraftItemStack(ItemStack item) {
      this(item.getType(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
   }

   private CraftItemStack(Material type, int amount, short durability, ItemMeta itemMeta) {
      this.setType(type);
      this.setAmount(amount);
      this.setDurability(durability);
      this.setItemMeta(itemMeta);
   }

   @Internal
   public boolean isForInventoryDrop() {
      return this.isForInventoryDrop;
   }

   @Internal
   public ItemStack markForInventoryDrop() {
      this.isForInventoryDrop = true;
      return this;
   }

   public MaterialData getData() {
      return this.handle != null ? CraftMagicNumbers.getMaterialData(this.handle.getItem()) : super.getData();
   }

   public Material getType() {
      return this.handle != null ? CraftItemType.minecraftToBukkit(this.handle.getItem()) : Material.AIR;
   }

   public void setType(Material type) {
      if (this.getType() != type) {
         if (type == Material.AIR) {
            this.handle = null;
         } else if (CraftItemType.bukkitToMinecraft(type) == null) {
            this.handle = null;
         } else if (this.handle == null) {
            this.handle = new net.minecraft.world.item.ItemStack(CraftItemType.bukkitToMinecraft(type), 1);
         } else {
            this.handle.setItem(CraftItemType.bukkitToMinecraft(type));
            if (this.hasItemMeta()) {
               setItemMeta(this.handle, getItemMeta(this.handle));
            }
         }

         this.setData((MaterialData)null);
      }
   }

   public int getAmount() {
      return this.handle != null ? this.handle.getCount() : 0;
   }

   public void setAmount(int amount) {
      if (this.handle != null) {
         this.handle.setCount(amount);
         if (amount == 0) {
            this.handle = null;
         }

      }
   }

   public void setDurability(short durability) {
      if (this.handle != null) {
         this.handle.setDamageValue(durability);
      }

   }

   public short getDurability() {
      return this.handle != null ? (short)this.handle.getDamageValue() : -1;
   }

   public int getMaxStackSize() {
      return this.handle == null ? Material.AIR.getMaxStackSize() : this.handle.getMaxStackSize();
   }

   public void addUnsafeEnchantment(Enchantment ench, int level) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      if (makeTag(this.handle)) {
         ItemEnchantments list = getEnchantmentList(this.handle);
         if (list == null) {
            list = ItemEnchantments.EMPTY;
         }

         ItemEnchantments.Mutable listCopy = new ItemEnchantments.Mutable(list);
         listCopy.set(CraftEnchantment.bukkitToMinecraftHolder(ench), level);
         this.handle.set(DataComponents.ENCHANTMENTS, listCopy.toImmutable());
      }
   }

   static boolean makeTag(net.minecraft.world.item.ItemStack item) {
      return item != null;
   }

   public boolean containsEnchantment(Enchantment ench) {
      return this.getEnchantmentLevel(ench) > 0;
   }

   public int getEnchantmentLevel(Enchantment ench) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      return this.handle == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(CraftEnchantment.bukkitToMinecraftHolder(ench), this.handle);
   }

   public int removeEnchantment(Enchantment ench) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      ItemEnchantments list = getEnchantmentList(this.handle);
      if (list == null) {
         return 0;
      } else {
         int level = this.getEnchantmentLevel(ench);
         if (level <= 0) {
            return 0;
         } else {
            int size = list.size();
            if (size == 1) {
               this.handle.remove(DataComponents.ENCHANTMENTS);
               return level;
            } else {
               ItemEnchantments.Mutable listCopy = new ItemEnchantments.Mutable(list);
               listCopy.set(CraftEnchantment.bukkitToMinecraftHolder(ench), -1);
               this.handle.set(DataComponents.ENCHANTMENTS, listCopy.toImmutable());
               return level;
            }
         }
      }
   }

   public void removeEnchantments() {
      this.handle.remove(DataComponents.ENCHANTMENTS);
   }

   public Map<Enchantment, Integer> getEnchantments() {
      return getEnchantments(this.handle);
   }

   static Map<Enchantment, Integer> getEnchantments(net.minecraft.world.item.ItemStack item) {
      ItemEnchantments list = item != null && item.isEnchanted() ? (ItemEnchantments)item.get(DataComponents.ENCHANTMENTS) : null;
      if (list != null && list.size() != 0) {
         ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();
         list.entrySet().forEach((entry) -> {
            Holder<net.minecraft.world.item.enchantment.Enchantment> id = (Holder)entry.getKey();
            int level = entry.getIntValue();
            Enchantment enchant = CraftEnchantment.minecraftHolderToBukkit(id);
            if (enchant != null) {
               result.put(enchant, level);
            }

         });
         return result.build();
      } else {
         return ImmutableMap.of();
      }
   }

   static ItemEnchantments getEnchantmentList(net.minecraft.world.item.ItemStack item) {
      return item != null && item.isEnchanted() ? (ItemEnchantments)item.get(DataComponents.ENCHANTMENTS) : null;
   }

   public CraftItemStack clone() {
      CraftItemStack itemStack = (CraftItemStack)super.clone();
      if (this.handle != null) {
         itemStack.handle = this.handle.copy();
      }

      return itemStack;
   }

   public ItemMeta getItemMeta() {
      return getItemMeta(this.handle);
   }

   public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item) {
      return !hasItemMeta(item) ? CraftItemFactory.instance().getItemMeta(getType(item)) : ((CraftItemType)CraftItemType.minecraftToBukkitNew(item.getItem())).getItemMeta(item);
   }

   static Material getType(net.minecraft.world.item.ItemStack item) {
      return item == null ? Material.AIR : CraftItemType.minecraftToBukkit(item.getItem());
   }

   public boolean setItemMeta(ItemMeta itemMeta) {
      return setItemMeta(this.handle, itemMeta);
   }

   public static boolean setItemMeta(net.minecraft.world.item.ItemStack item, ItemMeta itemMeta) {
      if (item == null) {
         return false;
      } else if (CraftItemFactory.instance().equals((ItemMeta)itemMeta, (ItemMeta)null)) {
         item.restorePatch(DataComponentPatch.EMPTY);
         return true;
      } else if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) {
         return false;
      } else {
         itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
         if (itemMeta == null) {
            return true;
         } else {
            if (!((CraftMetaItem)itemMeta).isEmpty()) {
               CraftMetaItem.Applicator tag = new CraftMetaItem.Applicator();
               ((CraftMetaItem)itemMeta).applyToItem(tag);
               item.restorePatch(tag.build());
            }

            if (item.getItem() != null && item.getMaxDamage() > 0) {
               item.setDamageValue(item.getDamageValue());
            }

            return true;
         }
      }
   }

   public boolean isSimilar(ItemStack stack) {
      if (stack == null) {
         return false;
      } else if (stack == this) {
         return true;
      } else if (!(stack instanceof CraftItemStack)) {
         return stack.getClass() == ItemStack.class && stack.isSimilar(this);
      } else {
         CraftItemStack that = (CraftItemStack)stack;
         if (this.handle == that.handle) {
            return true;
         } else if (this.handle != null && that.handle != null) {
            Material comparisonType = CraftLegacy.fromLegacy(that.getType());
            if (comparisonType == this.getType() && this.getDurability() == that.getDurability()) {
               return this.hasItemMeta() ? that.hasItemMeta() && this.handle.getComponents().equals(that.handle.getComponents()) : !that.hasItemMeta();
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public boolean hasItemMeta() {
      return hasItemMeta(this.handle) && !CraftItemFactory.instance().equals((ItemMeta)this.getItemMeta(), (ItemMeta)null);
   }

   static boolean hasItemMeta(net.minecraft.world.item.ItemStack item) {
      return item != null && !item.getComponentsPatch().isEmpty();
   }
}
