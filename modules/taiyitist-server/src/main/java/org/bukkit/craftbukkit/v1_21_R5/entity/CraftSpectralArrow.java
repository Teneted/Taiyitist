package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.SpectralArrow;

public class CraftSpectralArrow extends CraftAbstractArrow implements SpectralArrow {
   public CraftSpectralArrow(CraftServer server, net.minecraft.world.entity.projectile.SpectralArrow entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.projectile.SpectralArrow getHandle() {
      return (net.minecraft.world.entity.projectile.SpectralArrow)this.entity;
   }

   public String toString() {
      return "CraftSpectralArrow";
   }

   public int getGlowingTicks() {
      return this.getHandle().duration;
   }

   public void setGlowingTicks(int duration) {
      this.getHandle().duration = duration;
   }
}
