package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.AgeableMob;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ageable;

public class CraftAgeable extends CraftCreature implements Ageable {
   public CraftAgeable(CraftServer server, AgeableMob entity) {
      super(server, entity);
   }

   public int getAge() {
      return this.getHandle().getAge();
   }

   public void setAge(int age) {
      this.getHandle().setAge(age);
   }

   public void setAgeLock(boolean lock) {
      this.getHandle().ageLocked = lock;
   }

   public boolean getAgeLock() {
      return this.getHandle().ageLocked;
   }

   public void setBaby() {
      if (this.isAdult()) {
         this.setAge(-24000);
      }

   }

   public void setAdult() {
      if (!this.isAdult()) {
         this.setAge(0);
      }

   }

   public boolean isAdult() {
      return this.getAge() >= 0;
   }

   public boolean canBreed() {
      return this.getAge() == 0;
   }

   public void setBreed(boolean breed) {
      if (breed) {
         this.setAge(0);
      } else if (this.isAdult()) {
         this.setAge(6000);
      }

   }

   public AgeableMob getHandle() {
      return (AgeableMob)this.entity;
   }

   public String toString() {
      return "CraftAgeable";
   }
}
