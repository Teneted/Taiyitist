package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftCoralFanAbstract extends CraftBlockData implements Waterlogged {
   private static final BooleanProperty WATERLOGGED = getBoolean(BaseCoralFanBlock.class, "waterlogged");

   public CraftCoralFanAbstract() {
   }

   public CraftCoralFanAbstract(BlockState state) {
      super(state);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
