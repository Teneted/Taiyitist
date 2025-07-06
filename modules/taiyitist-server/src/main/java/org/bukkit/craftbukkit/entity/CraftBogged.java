package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

public class CraftBogged extends CraftAbstractSkeleton implements Bogged {
   public CraftBogged(CraftServer server, net.minecraft.world.entity.monster.Bogged entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Bogged getHandle() {
      return (net.minecraft.world.entity.monster.Bogged)this.entity;
   }

   public String toString() {
      return "CraftBogged";
   }

   public Skeleton.SkeletonType getSkeletonType() {
      return SkeletonType.BOGGED;
   }

   public boolean isSheared() {
      return this.getHandle().isSheared();
   }

   public void setSheared(boolean flag) {
      this.getHandle().setSheared(flag);
   }
}
