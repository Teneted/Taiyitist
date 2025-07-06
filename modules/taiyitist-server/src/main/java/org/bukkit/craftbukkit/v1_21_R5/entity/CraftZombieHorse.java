package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
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
