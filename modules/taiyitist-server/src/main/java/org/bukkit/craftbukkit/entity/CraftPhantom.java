package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Phantom;

public class CraftPhantom extends CraftMob implements Phantom, CraftEnemy {
   public CraftPhantom(CraftServer server, net.minecraft.world.entity.monster.Phantom entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Phantom getHandle() {
      return (net.minecraft.world.entity.monster.Phantom)super.getHandle();
   }

   public int getSize() {
      return this.getHandle().getPhantomSize();
   }

   public void setSize(int sz) {
      this.getHandle().setPhantomSize(sz);
   }

   public String toString() {
      return "CraftPhantom";
   }
}
