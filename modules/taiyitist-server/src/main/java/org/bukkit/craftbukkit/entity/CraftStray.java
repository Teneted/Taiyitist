package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Skeleton.SkeletonType;

public class CraftStray extends CraftAbstractSkeleton implements Stray {
   public CraftStray(CraftServer server, net.minecraft.world.entity.monster.Stray entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftStray";
   }

   public Skeleton.SkeletonType getSkeletonType() {
      return SkeletonType.STRAY;
   }
}
