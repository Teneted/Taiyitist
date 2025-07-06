package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Tadpole;

public class CraftTadpole extends CraftFish implements Tadpole {
   public CraftTadpole(CraftServer server, net.minecraft.world.entity.animal.frog.Tadpole entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.frog.Tadpole getHandle() {
      return (net.minecraft.world.entity.animal.frog.Tadpole)this.entity;
   }

   public String toString() {
      return "CraftTadpole";
   }

   public int getAge() {
      return this.getHandle().age;
   }

   public void setAge(int age) {
      this.getHandle().age = age;
   }
}
