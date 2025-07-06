package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.CreeperPowerEvent.PowerCause;

public class CraftCreeper extends CraftMonster implements Creeper {
   public CraftCreeper(CraftServer server, net.minecraft.world.entity.monster.Creeper entity) {
      super(server, entity);
   }

   public boolean isPowered() {
      return this.getHandle().isPowered();
   }

   public void setPowered(boolean powered) {
      CreeperPowerEvent.PowerCause cause = powered ? PowerCause.SET_ON : PowerCause.SET_OFF;
      if (this.getHandle().bridge$generation() || !this.callPowerEvent(cause)) {
         this.getHandle().setPowered(powered);
      }

   }

   private boolean callPowerEvent(CreeperPowerEvent.PowerCause cause) {
      CreeperPowerEvent event = new CreeperPowerEvent((Creeper)this.getHandle().getBukkitEntity(), cause);
      this.server.getPluginManager().callEvent(event);
      return event.isCancelled();
   }

   public void setMaxFuseTicks(int ticks) {
      Preconditions.checkArgument(ticks >= 0, "ticks < 0");
      this.getHandle().maxSwell = ticks;
   }

   public int getMaxFuseTicks() {
      return this.getHandle().maxSwell;
   }

   public void setFuseTicks(int ticks) {
      Preconditions.checkArgument(ticks >= 0, "ticks < 0");
      Preconditions.checkArgument(ticks <= this.getMaxFuseTicks(), "ticks > maxFuseTicks");
      this.getHandle().swell = ticks;
   }

   public int getFuseTicks() {
      return this.getHandle().swell;
   }

   public void setExplosionRadius(int radius) {
      Preconditions.checkArgument(radius >= 0, "radius < 0");
      this.getHandle().explosionRadius = radius;
   }

   public int getExplosionRadius() {
      return this.getHandle().explosionRadius;
   }

   public void explode() {
      this.getHandle().explodeCreeper();
   }

   public void ignite(Entity entity) {
      Preconditions.checkNotNull(entity, "entity cannot be null");
      this.getHandle().entityIgniter = ((CraftEntity)entity).getHandle();
      this.getHandle().ignite();
   }

   public void ignite() {
      this.getHandle().ignite();
   }

   public Entity getIgniter() {
      return this.getHandle().entityIgniter != null ? this.getHandle().entityIgniter.getBukkitEntity() : null;
   }

   public net.minecraft.world.entity.monster.Creeper getHandle() {
      return (net.minecraft.world.entity.monster.Creeper)this.entity;
   }

   public String toString() {
      return "CraftCreeper";
   }
}
