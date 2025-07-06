package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Horse.Variant;

public class CraftMule extends CraftChestedHorse implements Mule {
   public CraftMule(CraftServer server, net.minecraft.world.entity.animal.horse.Mule entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftMule";
   }

   public Horse.Variant getVariant() {
      return Variant.MULE;
   }
}
