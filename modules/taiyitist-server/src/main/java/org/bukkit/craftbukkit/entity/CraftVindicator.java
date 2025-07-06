package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
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
