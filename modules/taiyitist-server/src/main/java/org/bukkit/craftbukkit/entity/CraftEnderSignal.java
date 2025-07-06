package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.EnderSignal;
import org.bukkit.inventory.ItemStack;

public class CraftEnderSignal extends CraftEntity implements EnderSignal {
   public CraftEnderSignal(CraftServer server, EyeOfEnder entity) {
      super(server, entity);
   }

   public EyeOfEnder getHandle() {
      return (EyeOfEnder)this.entity;
   }

   public String toString() {
      return "CraftEnderSignal";
   }

   public Location getTargetLocation() {
      return CraftLocation.toBukkit(this.getHandle().target, this.getWorld(), this.getHandle().getYRot(), this.getHandle().getXRot());
   }

   public void setTargetLocation(Location location) {
      Preconditions.checkArgument(this.getWorld().equals(location.getWorld()), "Cannot target EnderSignal across worlds");
      this.getHandle().signalTo(CraftLocation.toVec3D(location));
   }

   public boolean getDropItem() {
      return this.getHandle().surviveAfterDeath;
   }

   public void setDropItem(boolean shouldDropItem) {
      this.getHandle().surviveAfterDeath = shouldDropItem;
   }

   public ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(this.getHandle().getItem());
   }

   public void setItem(ItemStack item) {
      this.getHandle().setItem(item != null ? CraftItemStack.asNMSCopy(item) : Items.ENDER_EYE.getDefaultInstance());
   }

   public int getDespawnTimer() {
      return this.getHandle().life;
   }

   public void setDespawnTimer(int time) {
      this.getHandle().life = time;
   }
}
