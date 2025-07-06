package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftTintedParticleLeaves extends CraftBlockData implements Leaves, Waterlogged {
   private static final IntegerProperty DISTANCE = getInteger(TintedParticleLeavesBlock.class, "distance");
   private static final BooleanProperty PERSISTENT = getBoolean(TintedParticleLeavesBlock.class, "persistent");
   private static final BooleanProperty WATERLOGGED = getBoolean(TintedParticleLeavesBlock.class, "waterlogged");

   public CraftTintedParticleLeaves() {
   }

   public CraftTintedParticleLeaves(BlockState state) {
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
