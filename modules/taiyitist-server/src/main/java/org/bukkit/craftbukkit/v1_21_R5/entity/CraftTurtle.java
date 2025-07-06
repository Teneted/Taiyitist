package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Turtle;

public class CraftTurtle extends CraftAnimals implements Turtle {
   public CraftTurtle(CraftServer server, net.minecraft.world.entity.animal.Turtle entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Turtle getHandle() {
      return (net.minecraft.world.entity.animal.Turtle)super.getHandle();
   }

   public String toString() {
      return "CraftTurtle";
   }

   public boolean hasEgg() {
      return this.getHandle().hasEgg();
   }

   public boolean isLayingEgg() {
      return this.getHandle().isLayingEgg();
   }
}
