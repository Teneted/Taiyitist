package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Vindicator;

public class CraftVindicator extends CraftIllager implements Vindicator {
   public CraftVindicator(CraftServer server, net.minecraft.world.entity.monster.Vindicator entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Vindicator getHandle() {
      return (net.minecraft.world.entity.monster.Vindicator)super.getHandle();
   }

   public String toString() {
      return "CraftVindicator";
   }

   public boolean isJohnny() {
      return this.getHandle().isJohnny;
   }

   public void setJohnny(boolean johnny) {
      this.getHandle().isJohnny = johnny;
   }
}
