package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.ElderGuardian;

public class CraftElderGuardian extends CraftGuardian implements ElderGuardian {
   public CraftElderGuardian(CraftServer server, net.minecraft.world.entity.monster.ElderGuardian entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftElderGuardian";
   }

   public boolean isElder() {
      return true;
   }
}
