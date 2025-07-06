package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Bat;

public class CraftBat extends CraftAmbient implements Bat {
   public CraftBat(CraftServer server, net.minecraft.world.entity.ambient.Bat entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.ambient.Bat getHandle() {
      return (net.minecraft.world.entity.ambient.Bat)this.entity;
   }

   public String toString() {
      return "CraftBat";
   }

   public boolean isAwake() {
      return !this.getHandle().isResting();
   }

   public void setAwake(boolean state) {
      this.getHandle().setResting(!state);
   }
}
