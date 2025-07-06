package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftCoralFan extends CraftBlockData implements Waterlogged {
   private static final BooleanProperty WATERLOGGED = getBoolean(CoralFanBlock.class, "waterlogged");

   public CraftCoralFan() {
   }

   public CraftCoralFan(BlockState state) {
      super(state);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
