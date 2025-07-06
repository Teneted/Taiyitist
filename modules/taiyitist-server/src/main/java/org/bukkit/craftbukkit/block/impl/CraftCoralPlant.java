package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftCoralPlant extends CraftBlockData implements Waterlogged {
   private static final BooleanProperty WATERLOGGED = getBoolean(CoralPlantBlock.class, "waterlogged");

   public CraftCoralPlant() {
   }

   public CraftCoralPlant(BlockState state) {
      super(state);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
