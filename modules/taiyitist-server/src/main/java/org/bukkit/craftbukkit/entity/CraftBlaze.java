package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Blaze;

public class CraftBlaze extends CraftMonster implements Blaze {
   public CraftBlaze(CraftServer server, net.minecraft.world.entity.monster.Blaze entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Blaze getHandle() {
      return (net.minecraft.world.entity.monster.Blaze)this.entity;
   }

   public String toString() {
      return "CraftBlaze";
   }
}
