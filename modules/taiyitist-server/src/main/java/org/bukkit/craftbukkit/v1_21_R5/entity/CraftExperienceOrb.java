package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.ExperienceOrb;

public class CraftExperienceOrb extends CraftEntity implements ExperienceOrb {
   public CraftExperienceOrb(CraftServer server, net.minecraft.world.entity.ExperienceOrb entity) {
      super(server, entity);
   }

   public int getExperience() {
      return this.getHandle().getValue();
   }

   public void setExperience(int value) {
      this.getHandle().setValue(value);
   }

   public net.minecraft.world.entity.ExperienceOrb getHandle() {
      return (net.minecraft.world.entity.ExperienceOrb)this.entity;
   }

   public String toString() {
      return "CraftExperienceOrb";
   }
}
