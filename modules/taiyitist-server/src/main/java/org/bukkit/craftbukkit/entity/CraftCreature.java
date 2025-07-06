package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Creature;

public class CraftCreature extends CraftMob implements Creature {
   public CraftCreature(CraftServer server, PathfinderMob entity) {
      super(server, entity);
   }

   public PathfinderMob getHandle() {
      return (PathfinderMob)this.entity;
   }

   public String toString() {
      return "CraftCreature";
   }
}
