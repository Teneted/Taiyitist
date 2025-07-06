package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftUntintedParticleLeaves extends CraftBlockData implements Leaves, Waterlogged {
   private static final IntegerProperty DISTANCE = getInteger(UntintedParticleLeavesBlock.class, "distance");
   private static final BooleanProperty PERSISTENT = getBoolean(UntintedParticleLeavesBlock.class, "persistent");
   private static final BooleanProperty WATERLOGGED = getBoolean(UntintedParticleLeavesBlock.class, "waterlogged");

   public CraftUntintedParticleLeaves() {
   }

   public CraftUntintedParticleLeaves(BlockState state) {
      super(state);
   }

   public boolean isPersistent() {
      return (Boolean)this.get(PERSISTENT);
   }

   public void setPersistent(boolean persistent) {
      this.set(PERSISTENT, persistent);
   }

   public int getDistance() {
      return (Integer)this.get(DISTANCE);
   }

   public void setDistance(int distance) {
      this.set(DISTANCE, distance);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
