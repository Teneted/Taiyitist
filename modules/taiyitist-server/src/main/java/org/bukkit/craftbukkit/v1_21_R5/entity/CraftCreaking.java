package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLocation;
import org.bukkit.entity.Creaking;
import org.bukkit.entity.Player;

public class CraftCreaking extends CraftMonster implements Creaking {
   public CraftCreaking(CraftServer server, net.minecraft.world.entity.monster.creaking.Creaking entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.creaking.Creaking getHandle() {
      return (net.minecraft.world.entity.monster.creaking.Creaking)this.entity;
   }

   public Location getHome() {
      return CraftLocation.toBukkit(this.getHandle().getHomePos(), this.getHandle().level());
   }

   public void setHome(Location location) {
      Preconditions.checkArgument(location != null, "location cannot be null");
      Preconditions.checkArgument(this.getWorld().equals(location.getWorld()), "Home must be in the same world as the creaking");
      this.getHandle().setHomePos(CraftLocation.toBlockPosition(location));
   }

   public void activate(Player player) {
      Preconditions.checkArgument(player != null, "player cannot be null");
      this.getHandle().activate(((CraftPlayer)player).getHandle());
   }

   public void deactivate() {
      this.getHandle().deactivate();
   }

   public boolean isActive() {
      return this.getHandle().isActive();
   }

   public String toString() {
      return "CraftCreaking";
   }
}
