package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Light;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftLight extends CraftBlockData implements Light, Levelled, Waterlogged {
   private static final IntegerProperty LEVEL = getInteger(LightBlock.class, "level");
   private static final BooleanProperty WATERLOGGED = getBoolean(LightBlock.class, "waterlogged");

   public CraftLight() {
   }

   public CraftLight(BlockState state) {
      super(state);
   }

   public int getLevel() {
      return (Integer)this.get(LEVEL);
   }

   public void setLevel(int level) {
      this.set(LEVEL, level);
   }

   public int getMaximumLevel() {
      return getMax(LEVEL);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
