package org.bukkit.craftbukkit.v1_21_R5.entity.boat;

import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftBoat;
import org.bukkit.entity.boat.AcaciaBoat;

public class CraftAcaciaBoat extends CraftBoat implements AcaciaBoat {
   public CraftAcaciaBoat(CraftServer server, AbstractBoat entity) {
      super(server, entity);
   }
}
