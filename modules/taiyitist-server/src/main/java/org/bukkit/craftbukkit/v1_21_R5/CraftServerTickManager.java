package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import net.minecraft.server.ServerTickRateManager;
import org.bukkit.ServerTickManager;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.entity.Entity;

final class CraftServerTickManager implements ServerTickManager {
   private final ServerTickRateManager manager;

   CraftServerTickManager(ServerTickRateManager manager) {
      this.manager = manager;
   }

   public boolean isRunningNormally() {
      return this.manager.runsNormally();
   }

   public boolean isStepping() {
      return this.manager.isSteppingForward();
   }

   public boolean isSprinting() {
      return this.manager.isSprinting();
   }

   public boolean isFrozen() {
      return this.manager.isFrozen();
   }

   public float getTickRate() {
      return this.manager.tickrate();
   }

   public void setTickRate(float tickRate) {
      Preconditions.checkArgument(tickRate >= 1.0F && tickRate <= 10000.0F, "The given tick rate must not be less than 1.0 or greater than 10,000.0");
      this.manager.setTickRate(tickRate);
   }

   public void setFrozen(boolean frozen) {
      if (frozen) {
         if (this.manager.isSprinting()) {
            this.manager.stopSprinting();
         }

         if (this.manager.isSteppingForward()) {
            this.manager.stopStepping();
         }
      }

      this.manager.setFrozen(frozen);
   }

   public boolean stepGameIfFrozen(int ticks) {
      return this.manager.stepGameIfPaused(ticks);
   }

   public boolean stopStepping() {
      return this.manager.stopStepping();
   }

   public boolean requestGameToSprint(int ticks) {
      return this.manager.requestGameToSprint(ticks);
   }

   public boolean stopSprinting() {
      return this.manager.stopSprinting();
   }

   public boolean isFrozen(Entity entity) {
      Preconditions.checkArgument(entity != null, "The given entity must not be null");
      return this.manager.isEntityFrozen(((CraftEntity)entity).getHandle());
   }

   public int getFrozenTicksToRun() {
      return this.manager.frozenTicksToRun();
   }
}
