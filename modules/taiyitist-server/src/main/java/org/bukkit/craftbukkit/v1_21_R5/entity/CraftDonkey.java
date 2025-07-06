package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;

public class CraftDonkey extends CraftChestedHorse implements Donkey {
   public CraftDonkey(CraftServer server, net.minecraft.world.entity.animal.horse.Donkey entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftDonkey";
   }

   public Horse.Variant getVariant() {
      return Variant.DONKEY;
   }
}
