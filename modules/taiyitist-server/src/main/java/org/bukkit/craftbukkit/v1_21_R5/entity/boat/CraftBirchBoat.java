package org.bukkit.craftbukkit.v1_21_R5.entity.boat;

import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftBoat;
import org.bukkit.entity.boat.BirchBoat;

public class CraftBirchBoat extends CraftBoat implements BirchBoat {
   public CraftBirchBoat(CraftServer server, AbstractBoat entity) {
      super(server, entity);
   }
}
