package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftMangroveRoots extends CraftBlockData implements Waterlogged {
   private static final BooleanProperty WATERLOGGED = getBoolean(MangroveRootsBlock.class, "waterlogged");

   public CraftMangroveRoots() {
   }

   public CraftMangroveRoots(BlockState state) {
      super(state);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
