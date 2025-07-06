package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Vehicle;

public abstract class CraftVehicle extends CraftEntity implements Vehicle {
   public CraftVehicle(CraftServer server, Entity entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftVehicle{passenger=" + String.valueOf(this.getPassenger()) + "}";
   }
}
