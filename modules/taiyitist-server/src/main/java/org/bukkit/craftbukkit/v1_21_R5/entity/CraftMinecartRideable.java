package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.vehicle.Minecart;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.minecart.RideableMinecart;

public class CraftMinecartRideable extends CraftMinecart implements RideableMinecart {
   public CraftMinecartRideable(CraftServer server, Minecart entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftMinecartRideable";
   }
}
