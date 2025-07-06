package org.bukkit.craftbukkit.entity;

import net.minecraft.core.Rotations;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class CraftArmorStand extends CraftLivingEntity implements ArmorStand {
   public CraftArmorStand(CraftServer server, net.minecraft.world.entity.decoration.ArmorStand entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftArmorStand";
   }

   public net.minecraft.world.entity.decoration.ArmorStand getHandle() {
      return (net.minecraft.world.entity.decoration.ArmorStand)super.getHandle();
   }

   public ItemStack getItemInHand() {
      return this.getEquipment().getItemInHand();
   }

   public void setItemInHand(ItemStack item) {
      this.getEquipment().setItemInHand(item);
   }

   public ItemStack getBoots() {
      return this.getEquipment().getBoots();
   }

   public void setBoots(ItemStack item) {
      this.getEquipment().setBoots(item);
   }

   public ItemStack getLeggings() {
      return this.getEquipment().getLeggings();
   }

   public void setLeggings(ItemStack item) {
      this.getEquipment().setLeggings(item);
   }

   public ItemStack getChestplate() {
      return this.getEquipment().getChestplate();
   }

   public void setChestplate(ItemStack item) {
      this.getEquipment().setChestplate(item);
   }

   public ItemStack getHelmet() {
      return this.getEquipment().getHelmet();
   }

   public void setHelmet(ItemStack item) {
      this.getEquipment().setHelmet(item);
   }

   public EulerAngle getBodyPose() {
      return fromNMS(this.getHandle().getBodyPose());
   }

   public void setBodyPose(EulerAngle pose) {
      this.getHandle().setBodyPose(toNMS(pose));
   }

   public EulerAngle getLeftArmPose() {
      return fromNMS(this.getHandle().getLeftArmPose());
   }

   public void setLeftArmPose(EulerAngle pose) {
      this.getHandle().setLeftArmPose(toNMS(pose));
   }

   public EulerAngle getRightArmPose() {
      return fromNMS(this.getHandle().getRightArmPose());
   }

   public void setRightArmPose(EulerAngle pose) {
      this.getHandle().setRightArmPose(toNMS(pose));
   }

   public EulerAngle getLeftLegPose() {
      return fromNMS(this.getHandle().getLeftLegPose());
   }

   public void setLeftLegPose(EulerAngle pose) {
      this.getHandle().setLeftLegPose(toNMS(pose));
   }

   public EulerAngle getRightLegPose() {
      return fromNMS(this.getHandle().getRightLegPose());
   }

   public void setRightLegPose(EulerAngle pose) {
      this.getHandle().setRightLegPose(toNMS(pose));
   }

   public EulerAngle getHeadPose() {
      return fromNMS(this.getHandle().getHeadPose());
   }

   public void setHeadPose(EulerAngle pose) {
      this.getHandle().setHeadPose(toNMS(pose));
   }

   public boolean hasBasePlate() {
      return this.getHandle().showBasePlate();
   }

   public void setBasePlate(boolean basePlate) {
      this.getHandle().setNoBasePlate(!basePlate);
   }

   public void setGravity(boolean gravity) {
      super.setGravity(gravity);
      this.getHandle().noPhysics = !gravity;
   }

   public boolean isVisible() {
      return !this.getHandle().isInvisible();
   }

   public void setVisible(boolean visible) {
      this.getHandle().setInvisible(!visible);
   }

   public boolean hasArms() {
      return this.getHandle().showArms();
   }

   public void setArms(boolean arms) {
      this.getHandle().setShowArms(arms);
   }

   public boolean isSmall() {
      return this.getHandle().isSmall();
   }

   public void setSmall(boolean small) {
      this.getHandle().setSmall(small);
   }

   private static EulerAngle fromNMS(Rotations old) {
      return new EulerAngle(Math.toRadians((double)old.x()), Math.toRadians((double)old.y()), Math.toRadians((double)old.z()));
   }

   private static Rotations toNMS(EulerAngle old) {
      return new Rotations((float)Math.toDegrees(old.getX()), (float)Math.toDegrees(old.getY()), (float)Math.toDegrees(old.getZ()));
   }

   public boolean isMarker() {
      return this.getHandle().isMarker();
   }

   public void setMarker(boolean marker) {
      this.getHandle().setMarker(marker);
   }

   public void addEquipmentLock(EquipmentSlot equipmentSlot, ArmorStand.LockType lockType) {
      net.minecraft.world.entity.decoration.ArmorStand var10000 = this.getHandle();
      var10000.disabledSlots |= 1 << CraftEquipmentSlot.getNMS(equipmentSlot).getFilterBit(lockType.ordinal() * 8);
   }

   public void removeEquipmentLock(EquipmentSlot equipmentSlot, ArmorStand.LockType lockType) {
      net.minecraft.world.entity.decoration.ArmorStand var10000 = this.getHandle();
      var10000.disabledSlots &= ~(1 << CraftEquipmentSlot.getNMS(equipmentSlot).getFilterBit(lockType.ordinal() * 8));
   }

   public boolean hasEquipmentLock(EquipmentSlot equipmentSlot, ArmorStand.LockType lockType) {
      return (this.getHandle().disabledSlots & 1 << CraftEquipmentSlot.getNMS(equipmentSlot).getFilterBit(lockType.ordinal() * 8)) != 0;
   }
}
