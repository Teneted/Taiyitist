package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Slime;

public class CraftSlime extends CraftMob implements Slime, CraftEnemy {
   public CraftSlime(CraftServer server, net.minecraft.world.entity.monster.Slime entity) {
      super(server, entity);
   }

   public int getSize() {
      return this.getHandle().getSize();
   }

   public void setSize(int size) {
      this.getHandle().setSize(size, true);
   }

   public net.minecraft.world.entity.monster.Slime getHandle() {
      return (net.minecraft.world.entity.monster.Slime)this.entity;
   }

   public String toString() {
      return "CraftSlime";
   }
}
