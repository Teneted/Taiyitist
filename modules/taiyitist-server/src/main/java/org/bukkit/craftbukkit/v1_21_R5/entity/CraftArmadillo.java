package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Armadillo;

public class CraftArmadillo extends CraftAnimals implements Armadillo {
   public CraftArmadillo(CraftServer server, net.minecraft.world.entity.animal.armadillo.Armadillo entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.armadillo.Armadillo getHandle() {
      return (net.minecraft.world.entity.animal.armadillo.Armadillo)super.getHandle();
   }

   public String toString() {
      return "CraftArmadillo";
   }
}
