package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.animal.SnowGolem;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Snowman;

public class CraftSnowman extends CraftGolem implements Snowman {
   public CraftSnowman(CraftServer server, SnowGolem entity) {
      super(server, entity);
   }

   public boolean isDerp() {
      return !this.getHandle().hasPumpkin();
   }

   public void setDerp(boolean derpMode) {
      this.getHandle().setPumpkin(!derpMode);
   }

   public SnowGolem getHandle() {
      return (SnowGolem)this.entity;
   }

   public String toString() {
      return "CraftSnowman";
   }
}
