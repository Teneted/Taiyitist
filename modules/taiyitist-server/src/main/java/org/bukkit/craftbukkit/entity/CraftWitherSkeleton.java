package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

public class CraftWitherSkeleton extends CraftAbstractSkeleton implements WitherSkeleton {
   public CraftWitherSkeleton(CraftServer server, net.minecraft.world.entity.monster.WitherSkeleton entity) {
      super(server, entity);
   }

   public String toString() {
      return "CraftWitherSkeleton";
   }

   public Skeleton.SkeletonType getSkeletonType() {
      return SkeletonType.WITHER;
   }
}
