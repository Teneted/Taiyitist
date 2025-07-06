package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CraftEntityEquipment implements EntityEquipment {
   private final CraftLivingEntity entity;

   public CraftEntityEquipment(CraftLivingEntity entity) {
      this.entity = entity;
   }

   public void setItem(EquipmentSlot slot, ItemStack item) {
      this.setItem(slot, item, false);
   }

   public void setItem(EquipmentSlot slot, ItemStack item, boolean silent) {
      Preconditions.checkArgument(slot != null, "slot must not be null");
      net.minecraft.world.entity.EquipmentSlot nmsSlot = CraftEquipmentSlot.getNMS(slot);
      this.setEquipment(nmsSlot, item, silent);
   }

   public ItemStack getItem(EquipmentSlot slot) {
      Preconditions.checkArgument(slot != null, "slot must not be null");
      net.minecraft.world.entity.EquipmentSlot nmsSlot = CraftEquipmentSlot.getNMS(slot);
      return this.getEquipment(nmsSlot);
   }

   public ItemStack getItemInMainHand() {
      return this.getEquipment(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
   }

   public void setItemInMainHand(ItemStack item) {
      this.setItemInMainHand(item, false);
   }

   public void setItemInMainHand(ItemStack item, boolean silent) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.MAINHAND, item, silent);
   }

   public ItemStack getItemInOffHand() {
      return this.getEquipment(net.minecraft.world.entity.EquipmentSlot.OFFHAND);
   }

   public void setItemInOffHand(ItemStack item) {
      this.setItemInOffHand(item, false);
   }

   public void setItemInOffHand(ItemStack item, boolean silent) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.OFFHAND, item, silent);
   }

   public ItemStack getItemInHand() {
      return this.getItemInMainHand();
   }

   public void setItemInHand(ItemStack stack) {
      this.setItemInMainHand(stack);
   }

   public ItemStack getHelmet() {
      return this.getEquipment(net.minecraft.world.entity.EquipmentSlot.HEAD);
   }

   public void setHelmet(ItemStack helmet) {
      this.setHelmet(helmet, false);
   }

   public void setHelmet(ItemStack helmet, boolean silent) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.HEAD, helmet, silent);
   }

   public ItemStack getChestplate() {
      return this.getEquipment(net.minecraft.world.entity.EquipmentSlot.CHEST);
   }

   public void setChestplate(ItemStack chestplate) {
      this.setChestplate(chestplate, false);
   }

   public void setChestplate(ItemStack chestplate, boolean silent) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.CHEST, chestplate, silent);
   }

   public ItemStack getLeggings() {
      return this.getEquipment(net.minecraft.world.entity.EquipmentSlot.LEGS);
   }

   public void setLeggings(ItemStack leggings) {
      this.setLeggings(leggings, false);
   }

   public void setLeggings(ItemStack leggings, boolean silent) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.LEGS, leggings, silent);
   }

   public ItemStack getBoots() {
      return this.getEquipment(net.minecraft.world.entity.EquipmentSlot.FEET);
   }

   public void setBoots(ItemStack boots) {
      this.setBoots(boots, false);
   }

   public void setBoots(ItemStack boots, boolean silent) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.FEET, boots, silent);
   }

   public ItemStack[] getArmorContents() {
      ItemStack[] armor = new ItemStack[]{this.getEquipment(net.minecraft.world.entity.EquipmentSlot.FEET), this.getEquipment(net.minecraft.world.entity.EquipmentSlot.LEGS), this.getEquipment(net.minecraft.world.entity.EquipmentSlot.CHEST), this.getEquipment(net.minecraft.world.entity.EquipmentSlot.HEAD)};
      return armor;
   }

   public void setArmorContents(ItemStack[] items) {
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.FEET, items.length >= 1 ? items[0] : null, false);
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.LEGS, items.length >= 2 ? items[1] : null, false);
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.CHEST, items.length >= 3 ? items[2] : null, false);
      this.setEquipment(net.minecraft.world.entity.EquipmentSlot.HEAD, items.length >= 4 ? items[3] : null, false);
   }

   private ItemStack getEquipment(net.minecraft.world.entity.EquipmentSlot slot) {
      return CraftItemStack.asBukkitCopy(this.entity.getHandle().getItemBySlot(slot));
   }

   private void setEquipment(net.minecraft.world.entity.EquipmentSlot slot, ItemStack stack, boolean silent) {
      this.entity.getHandle().setItemSlot(slot, CraftItemStack.asNMSCopy(stack), silent);
   }

   public void clear() {
      net.minecraft.world.entity.EquipmentSlot[] var1 = net.minecraft.world.entity.EquipmentSlot.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         net.minecraft.world.entity.EquipmentSlot slot = var1[var3];
         this.setEquipment(slot, (ItemStack)null, false);
      }

   }

   public Entity getHolder() {
      return this.entity;
   }

   public float getItemInHandDropChance() {
      return this.getItemInMainHandDropChance();
   }

   public void setItemInHandDropChance(float chance) {
      this.setItemInMainHandDropChance(chance);
   }

   public float getItemInMainHandDropChance() {
      return this.getDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
   }

   public void setItemInMainHandDropChance(float chance) {
      this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, chance);
   }

   public float getItemInOffHandDropChance() {
      return this.getDropChance(net.minecraft.world.entity.EquipmentSlot.OFFHAND);
   }

   public void setItemInOffHandDropChance(float chance) {
      this.setDropChance(net.minecraft.world.entity.EquipmentSlot.OFFHAND, chance);
   }

   public float getHelmetDropChance() {
      return this.getDropChance(net.minecraft.world.entity.EquipmentSlot.HEAD);
   }

   public void setHelmetDropChance(float chance) {
      this.setDropChance(net.minecraft.world.entity.EquipmentSlot.HEAD, chance);
   }

   public float getChestplateDropChance() {
      return this.getDropChance(net.minecraft.world.entity.EquipmentSlot.CHEST);
   }

   public void setChestplateDropChance(float chance) {
      this.setDropChance(net.minecraft.world.entity.EquipmentSlot.CHEST, chance);
   }

   public float getLeggingsDropChance() {
      return this.getDropChance(net.minecraft.world.entity.EquipmentSlot.LEGS);
   }

   public void setLeggingsDropChance(float chance) {
      this.setDropChance(net.minecraft.world.entity.EquipmentSlot.LEGS, chance);
   }

   public float getBootsDropChance() {
      return this.getDropChance(net.minecraft.world.entity.EquipmentSlot.FEET);
   }

   public void setBootsDropChance(float chance) {
      this.setDropChance(net.minecraft.world.entity.EquipmentSlot.FEET, chance);
   }

   private void setDropChance(net.minecraft.world.entity.EquipmentSlot slot, float chance) {
      Preconditions.checkArgument(this.entity.getHandle() instanceof Mob, "Cannot set drop chance for non-Mob entity");
      ((Mob)this.entity.getHandle()).setDropChance(slot, chance);
   }

   private float getDropChance(net.minecraft.world.entity.EquipmentSlot slot) {
      return !(this.entity.getHandle() instanceof Mob) ? 1.0F : ((Mob)this.entity.getHandle()).getDropChances().byEquipment(slot);
   }
}
