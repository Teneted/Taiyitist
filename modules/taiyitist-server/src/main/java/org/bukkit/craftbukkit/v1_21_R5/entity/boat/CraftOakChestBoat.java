package org.bukkit.craftbukkit.v1_21_R5.entity.boat;

import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftChestBoat;
import org.bukkit.entity.boat.OakChestBoat;

public class CraftOakChestBoat extends CraftChestBoat implements OakChestBoat {
   public CraftOakChestBoat(CraftServer server, AbstractChestBoat entity) {
      super(server, entity);
   }
}
