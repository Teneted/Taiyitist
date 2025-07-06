package org.bukkit.craftbukkit.v1_21_R5.entity.boat;

import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftChestBoat;
import org.bukkit.entity.boat.MangroveChestBoat;

public class CraftMangroveChestBoat extends CraftChestBoat implements MangroveChestBoat {
   public CraftMangroveChestBoat(CraftServer server, AbstractChestBoat entity) {
      super(server, entity);
   }
}
