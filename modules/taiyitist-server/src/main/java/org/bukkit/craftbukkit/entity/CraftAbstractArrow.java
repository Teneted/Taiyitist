package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.projectiles.ProjectileSource;

public class CraftAbstractArrow extends CraftProjectile implements AbstractArrow {
   public CraftAbstractArrow(CraftServer server, net.minecraft.world.entity.projectile.AbstractArrow entity) {
      super(server, entity);
   }

   public void setKnockbackStrength(int knockbackStrength) {
   }

   public int getKnockbackStrength() {
      return 0;
   }

   public double getDamage() {
      return this.getHandle().baseDamage;
   }

   public void setDamage(double damage) {
      Preconditions.checkArgument(damage >= 0.0, "Damage value (%s) must be positive", damage);
      this.getHandle().setBaseDamage(damage);
   }

   public int getPierceLevel() {
      return this.getHandle().getPierceLevel();
   }

   public void setPierceLevel(int pierceLevel) {
      Preconditions.checkArgument(0 <= pierceLevel && pierceLevel <= 127, "Pierce level (%s) out of range, expected 0 < level < 127", pierceLevel);
      this.getHandle().setPierceLevel((byte)pierceLevel);
   }

   public boolean isCritical() {
      return this.getHandle().isCritArrow();
   }

   public void setCritical(boolean critical) {
      this.getHandle().setCritArrow(critical);
   }

   public ProjectileSource getShooter() {
      return this.getHandle().bridge$projectileSource();
   }

   public void setShooter(ProjectileSource shooter) {
      if (shooter instanceof Entity) {
         this.getHandle().setOwner(((CraftEntity)shooter).getHandle());
      } else {
         this.getHandle().setOwner((net.minecraft.world.entity.Entity)null);
      }

      this.getHandle().taiyitist$setProjectileSource(shooter);
   }

   public boolean isInBlock() {
      return this.getHandle().isInGround();
   }

   public Block getAttachedBlock() {
      if (!this.isInBlock()) {
         return null;
      } else {
         BlockPos pos = this.getHandle().blockPosition();
         return this.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
      }
   }

   public AbstractArrow.PickupStatus getPickupStatus() {
      return PickupStatus.values()[this.getHandle().pickup.ordinal()];
   }

   public void setPickupStatus(AbstractArrow.PickupStatus status) {
      Preconditions.checkArgument(status != null, "PickupStatus cannot be null");
      this.getHandle().pickup = Pickup.byOrdinal(status.ordinal());
   }

   public void setTicksLived(int value) {
      super.setTicksLived(value);
      this.getHandle().life = value;
   }

   public boolean isShotFromCrossbow() {
      ItemStack firedFromWeapon = this.getHandle().getWeaponItem();
      return firedFromWeapon != null && firedFromWeapon.is(Items.CROSSBOW);
   }

   public void setShotFromCrossbow(boolean shotFromCrossbow) {
   }

   public org.bukkit.inventory.ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(this.getHandle().pickupItemStack);
   }

   public void setItem(org.bukkit.inventory.ItemStack item) {
      Preconditions.checkArgument(item != null, "ItemStack cannot be null");
      this.getHandle().pickupItemStack = CraftItemStack.asNMSCopy(item);
   }

   public org.bukkit.inventory.ItemStack getWeapon() {
      return CraftItemStack.asBukkitCopy(this.getHandle().getWeaponItem());
   }

   public void setWeapon(org.bukkit.inventory.ItemStack item) {
      Preconditions.checkArgument(item != null, "ItemStack cannot be null");
      this.getHandle().firedFromWeapon = CraftItemStack.asNMSCopy(item);
   }

   public net.minecraft.world.entity.projectile.AbstractArrow getHandle() {
      return (net.minecraft.world.entity.projectile.AbstractArrow)this.entity;
   }

   public String toString() {
      return "CraftArrow";
   }
}
