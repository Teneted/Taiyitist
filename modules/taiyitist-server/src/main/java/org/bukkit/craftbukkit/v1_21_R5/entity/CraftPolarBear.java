package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.PolarBear;

public class CraftPolarBear extends CraftAnimals implements PolarBear {
   public CraftPolarBear(CraftServer server, net.minecraft.world.entity.animal.PolarBear entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.PolarBear getHandle() {
      return (net.minecraft.world.entity.animal.PolarBear)this.entity;
   }

   public String toString() {
      return "CraftPolarBear";
   }
}
