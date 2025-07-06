package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.Horse.Variant;

public class CraftZombieHorse extends CraftAbstractHorse implements ZombieHorse {
   public CraftZombieHorse(CraftServer server, net.minecraft.world.entity.animal.horse.ZombieHorse entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftZombieHorse";
   }

   public Horse.Variant getVariant() {
      return Variant.UNDEAD_HORSE;
   }
}
