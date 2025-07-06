package org.bukkit.craftbukkit.v1_21_R5.entity.boat;

import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftBoat;
import org.bukkit.entity.boat.SpruceBoat;

public class CraftSpruceBoat extends CraftBoat implements SpruceBoat {
   public CraftSpruceBoat(CraftServer server, AbstractBoat entity) {
      super(server, entity);
   }
}
