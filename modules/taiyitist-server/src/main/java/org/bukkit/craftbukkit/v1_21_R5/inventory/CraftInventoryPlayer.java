package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CraftInventoryPlayer extends CraftInventory implements PlayerInventory, EntityEquipment {
   public CraftInventoryPlayer(Inventory inventory) {
      super(inventory);
   }

   public Inventory getInventory() {
      return (Inventory)this.inventory;
   }

   public int getSize() {
      return 41;
   }

   public ItemStack[] getStorageContents() {
      return this.asCraftMirror(this.getInventory().getNonEquipmentItems());
   }

   public ItemStack getItemInMainHand() {
      return CraftItemStack.asCraftMirror(this.getInventory().getSelectedItem());
   }

   public void setItemInMainHand(ItemStack item) {
      this.setItem(this.getHeldItemSlot(), item);
   }

   public void setItemInMainHand(ItemStack item, boolean silent) {
      this.setItemInMainHand(item);
   }

   public ItemStack getItemInOffHand() {
      return CraftItemStack.asCraftMirror(this.getInventory().getItem(40));
   }

   public void setItemInOffHand(ItemStack item) {
      ItemStack[] extra = this.getExtraContents();
      extra[0] = item;
      this.setExtraContents(extra);
   }

   public void setItemInOffHand(ItemStack item, boolean silent) {
      this.setItemInOffHand(item);
   }

   public ItemStack getItemInHand() {
      return this.getItemInMainHand();
   }

   public void setItemInHand(ItemStack stack) {
      this.setItemInMainHand(stack);
   }

   public void setItem(int index, ItemStack item) {
      super.setItem(index, item);
      if (this.getHolder() != null) {
         ServerPlayer player = ((CraftPlayer)this.getHolder()).getHandle();
         if (player.connection != null) {
            if (index < Inventory.getSelectionSize()) {
               index += 36;
            } else if (index > 39) {
               index += 5;
            } else if (index > 35) {
               index = 8 - (index - 36);
            }

            player.connection.send(new ClientboundContainerSetSlotPacket(player.inventoryMenu.containerId, player.inventoryMenu.incrementStateId(), index, CraftItemStack.asNMSCopy(item)));
         }
      }
   }

   public void setItem(EquipmentSlot slot, ItemStack item) {
      Preconditions.checkArgument(slot != null, "slot must not be null");
      switch (slot) {
         case HAND -> this.setItemInMainHand(item);
         case OFF_HAND -> this.setItemInOffHand(item);
         case FEET -> this.setBoots(item);
         case LEGS -> this.setLeggings(item);
         case CHEST -> this.setChestplate(item);
         case HEAD -> this.setHelmet(item);
         default -> throw new IllegalArgumentException("Could not set slot " + String.valueOf(slot) + " - not a valid slot for PlayerInventory");
      }

   }

   public void setItem(EquipmentSlot slot, ItemStack item, boolean silent) {
      this.setItem(slot, item);
   }

   public ItemStack getItem(EquipmentSlot slot) {
      Preconditions.checkArgument(slot != null, "slot must not be null");
      switch (slot) {
         case HAND -> {
            return this.getItemInMainHand();
         }
         case OFF_HAND -> {
            return this.getItemInOffHand();
         }
         case FEET -> {
            return this.getBoots();
         }
         case LEGS -> {
            return this.getLeggings();
         }
         case CHEST -> {
            return this.getChestplate();
         }
         case HEAD -> {
            return this.getHelmet();
         }
         default -> throw new IllegalArgumentException("Could not get slot " + String.valueOf(slot) + " - not a valid slot for PlayerInventory");
      }
   }

   public int getHeldItemSlot() {
      return this.getInventory().getSelectedSlot();
   }

   public void setHeldItemSlot(int slot) {
      Preconditions.checkArgument(slot >= 0 && slot < Inventory.getSelectionSize(), "Slot (%s) is not between 0 and %s inclusive", slot, Inventory.getSelectionSize() - 1);
      this.getInventory().setSelectedSlot(slot);
      ((CraftPlayer)this.getHolder()).getHandle().connection.send(new ClientboundSetHeldSlotPacket(slot));
   }

   public ItemStack getHelmet() {
      return this.getItem(net.minecraft.world.entity.EquipmentSlot.HEAD.getIndex(36));
   }

   public ItemStack getChestplate() {
      return this.getItem(net.minecraft.world.entity.EquipmentSlot.CHEST.getIndex(36));
   }

   public ItemStack getLeggings() {
      return this.getItem(net.minecraft.world.entity.EquipmentSlot.LEGS.getIndex(36));
   }

   public ItemStack getBoots() {
      return this.getItem(net.minecraft.world.entity.EquipmentSlot.FEET.getIndex(36));
   }

   public void setHelmet(ItemStack helmet) {
      this.setItem(net.minecraft.world.entity.EquipmentSlot.HEAD.getIndex(36), helmet);
   }

   public void setHelmet(ItemStack helmet, boolean silent) {
      this.setHelmet(helmet);
   }

   public void setChestplate(ItemStack chestplate) {
      this.setItem(net.minecraft.world.entity.EquipmentSlot.CHEST.getIndex(36), chestplate);
   }

   public void setChestplate(ItemStack chestplate, boolean silent) {
      this.setChestplate(chestplate);
   }

   public void setLeggings(ItemStack leggings) {
      this.setItem(net.minecraft.world.entity.EquipmentSlot.LEGS.getIndex(36), leggings);
   }

   public void setLeggings(ItemStack leggings, boolean silent) {
      this.setLeggings(leggings);
   }

   public void setBoots(ItemStack boots) {
      this.setItem(net.minecraft.world.entity.EquipmentSlot.FEET.getIndex(36), boots);
   }

   public void setBoots(ItemStack boots, boolean silent) {
      this.setBoots(boots);
   }

   public ItemStack[] getArmorContents() {
      return this.asCraftMirror(this.getInventory().getArmorContents());
   }

   private void setSlots(ItemStack[] items, int baseSlot, int length) {
      if (items == null) {
         items = new ItemStack[length];
      }

      Preconditions.checkArgument(items.length <= length, "items.length must be <= %s", length);

      for(int i = 0; i < length; ++i) {
         if (i >= items.length) {
            this.setItem(baseSlot + i, (ItemStack)null);
         } else {
            this.setItem(baseSlot + i, items[i]);
         }
      }

   }

   public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
      this.setSlots(items, 0, 36);
   }

   public void setArmorContents(ItemStack[] items) {
      this.setSlots(items, 36, 4);
   }

   public ItemStack[] getExtraContents() {
      return new ItemStack[]{this.getItemInOffHand()};
   }

   public void setExtraContents(ItemStack[] items) {
      this.setSlots(items, 40, 1);
   }

   public HumanEntity getHolder() {
      return (HumanEntity)this.inventory.getOwner();
   }

   public float getItemInHandDropChance() {
      return this.getItemInMainHandDropChance();
   }

   public void setItemInHandDropChance(float chance) {
      this.setItemInMainHandDropChance(chance);
   }

   public float getItemInMainHandDropChance() {
      return 1.0F;
   }

   public void setItemInMainHandDropChance(float chance) {
      throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
   }

   public float getItemInOffHandDropChance() {
      return 1.0F;
   }

   public void setItemInOffHandDropChance(float chance) {
      throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
   }

   public float getHelmetDropChance() {
      return 1.0F;
   }

   public void setHelmetDropChance(float chance) {
      throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
   }

   public float getChestplateDropChance() {
      return 1.0F;
   }

   public void setChestplateDropChance(float chance) {
      throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
   }

   public float getLeggingsDropChance() {
      return 1.0F;
   }

   public void setLeggingsDropChance(float chance) {
      throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
   }

   public float getBootsDropChance() {
      return 1.0F;
   }

   public void setBootsDropChance(float chance) {
      throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
   }
}
