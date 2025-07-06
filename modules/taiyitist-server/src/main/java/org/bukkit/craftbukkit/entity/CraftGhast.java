package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ghast;

public class CraftGhast extends CraftMob implements Ghast, CraftEnemy {
   public CraftGhast(CraftServer server, net.minecraft.world.entity.monster.Ghast entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Ghast getHandle() {
      return (net.minecraft.world.entity.monster.Ghast)this.entity;
   }

   public String toString() {
      return "CraftGhast";
   }

   public boolean isCharging() {
      return this.getHandle().isCharging();
   }

   public void setCharging(boolean flag) {
      this.getHandle().setCharging(flag);
   }
}
