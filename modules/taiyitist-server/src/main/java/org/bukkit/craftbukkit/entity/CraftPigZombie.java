package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.PigZombie;

public class CraftPigZombie extends CraftZombie implements PigZombie {
   public CraftPigZombie(CraftServer server, ZombifiedPiglin entity) {
      super(server, entity);
   }

   public int getAnger() {
      return this.getHandle().getRemainingPersistentAngerTime();
   }

   public void setAnger(int level) {
      this.getHandle().setRemainingPersistentAngerTime(level);
   }

   public void setAngry(boolean angry) {
      this.setAnger(angry ? 400 : 0);
   }

   public boolean isAngry() {
      return this.getAnger() > 0;
   }

   public ZombifiedPiglin getHandle() {
      return (ZombifiedPiglin)this.entity;
   }

   public String toString() {
      return "CraftPigZombie";
   }

   public boolean isConverting() {
      return false;
   }

   public int getConversionTime() {
      throw new UnsupportedOperationException("Not supported by this Entity.");
   }

   public void setConversionTime(int time) {
      throw new UnsupportedOperationException("Not supported by this Entity.");
   }
}
