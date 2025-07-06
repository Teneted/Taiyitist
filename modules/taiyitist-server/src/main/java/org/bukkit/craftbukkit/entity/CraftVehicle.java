package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Vehicle;

public abstract class CraftVehicle extends CraftEntity implements Vehicle {
   public CraftVehicle(CraftServer server, Entity entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftVehicle{passenger=" + String.valueOf(this.getPassenger()) + "}";
   }
}
