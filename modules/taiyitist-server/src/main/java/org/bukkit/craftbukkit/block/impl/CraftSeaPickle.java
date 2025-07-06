package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftSeaPickle extends CraftBlockData implements SeaPickle, Waterlogged {
   private static final IntegerProperty PICKLES = getInteger(SeaPickleBlock.class, "pickles");
   private static final BooleanProperty WATERLOGGED = getBoolean(SeaPickleBlock.class, "waterlogged");

   public CraftSeaPickle() {
   }

   public CraftSeaPickle(BlockState state) {
      super(state);
   }

   public int getPickles() {
      return (Integer)this.get(PICKLES);
   }

   public void setPickles(int pickles) {
      this.set(PICKLES, pickles);
   }

   public int getMinimumPickles() {
      return getMin(PICKLES);
   }

   public int getMaximumPickles() {
      return getMax(PICKLES);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
