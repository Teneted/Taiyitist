package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.AbstractWindCharge;
import org.bukkit.event.entity.EntityRemoveEvent.Cause;

public abstract class CraftAbstractWindCharge extends CraftFireball implements AbstractWindCharge {
   public CraftAbstractWindCharge(CraftServer server, net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge entity) {
      super(server, entity);
   }

   public void explode() {
      this.getHandle().explode(this.getHandle().position());
      this.getHandle().discard(Cause.EXPLODE);
   }

   public net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge getHandle() {
      return (net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge)super.getHandle();
   }

   public String toString() {
      return "CraftAbstractWindCharge";
   }
}
