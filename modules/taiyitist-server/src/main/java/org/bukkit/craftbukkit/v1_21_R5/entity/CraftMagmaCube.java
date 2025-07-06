package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.MagmaCube;

public class CraftMagmaCube extends CraftSlime implements MagmaCube {
   public CraftMagmaCube(CraftServer server, net.minecraft.world.entity.monster.MagmaCube entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.MagmaCube getHandle() {
      return (net.minecraft.world.entity.monster.MagmaCube)this.entity;
   }

   public String toString() {
      return "CraftMagmaCube";
   }
}
