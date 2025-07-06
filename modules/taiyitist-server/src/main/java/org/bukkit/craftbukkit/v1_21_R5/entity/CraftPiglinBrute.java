package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.PiglinBrute;

public class CraftPiglinBrute extends CraftPiglinAbstract implements PiglinBrute {
   public CraftPiglinBrute(CraftServer server, net.minecraft.world.entity.monster.piglin.PiglinBrute entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.piglin.PiglinBrute getHandle() {
      return (net.minecraft.world.entity.monster.piglin.PiglinBrute)super.getHandle();
   }

   public String toString() {
      return "CraftPiglinBrute";
   }
}
