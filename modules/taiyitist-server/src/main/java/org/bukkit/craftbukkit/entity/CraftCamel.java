package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.Pose;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;

public class CraftCamel extends CraftAbstractHorse implements Camel {
   public CraftCamel(CraftServer server, net.minecraft.world.entity.animal.camel.Camel entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.camel.Camel getHandle() {
      return (net.minecraft.world.entity.animal.camel.Camel)super.getHandle();
   }

   public String toString() {
      return "CraftCamel";
   }

   public Horse.Variant getVariant() {
      return Variant.CAMEL;
   }

   public boolean isDashing() {
      return this.getHandle().isDashing();
   }

   public void setDashing(boolean dashing) {
      this.getHandle().setDashing(dashing);
   }

   public boolean isSitting() {
      return this.getHandle().getPose() == Pose.SITTING;
   }

   public void setSitting(boolean sitting) {
      if (sitting) {
         this.getHandle().sitDown();
      } else {
         this.getHandle().standUp();
      }

   }
}
