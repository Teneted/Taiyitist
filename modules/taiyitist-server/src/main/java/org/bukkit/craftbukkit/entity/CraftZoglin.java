package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Zoglin;

public class CraftZoglin extends CraftMonster implements Zoglin {
   public CraftZoglin(CraftServer server, net.minecraft.world.entity.monster.Zoglin entity) {
      super(server, entity);
   }

   public boolean isBaby() {
      return this.getHandle().isBaby();
   }

   public void setBaby(boolean flag) {
      this.getHandle().setBaby(flag);
   }

   public net.minecraft.world.entity.monster.Zoglin getHandle() {
      return (net.minecraft.world.entity.monster.Zoglin)this.entity;
   }

   public String toString() {
      return "CraftZoglin";
   }

   public int getAge() {
      return this.getHandle().isBaby() ? -1 : 0;
   }

   public void setAge(int i) {
      this.getHandle().setBaby(i < 0);
   }

   public void setAgeLock(boolean b) {
   }

   public boolean getAgeLock() {
      return false;
   }

   public void setBaby() {
      this.getHandle().setBaby(true);
   }

   public void setAdult() {
      this.getHandle().setBaby(false);
   }

   public boolean isAdult() {
      return !this.getHandle().isBaby();
   }

   public boolean canBreed() {
      return false;
   }

   public void setBreed(boolean b) {
   }
}
