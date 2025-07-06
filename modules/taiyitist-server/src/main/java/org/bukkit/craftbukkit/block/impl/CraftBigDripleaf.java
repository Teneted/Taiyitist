package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.BigDripleaf;
import org.bukkit.block.data.type.Dripleaf;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftBigDripleaf extends CraftBlockData implements BigDripleaf, Dripleaf, Directional, Waterlogged {
   private static final CraftBlockStateEnum<?, BigDripleaf.Tilt> TILT = getEnum(BigDripleafBlock.class, "tilt", BigDripleaf.Tilt.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(BigDripleafBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(BigDripleafBlock.class, "waterlogged");

   public CraftBigDripleaf() {
   }

   public CraftBigDripleaf(BlockState state) {
      super(state);
   }

   public BigDripleaf.Tilt getTilt() {
      return (BigDripleaf.Tilt)this.get(TILT);
   }

   public void setTilt(BigDripleaf.Tilt tilt) {
      this.set(TILT, tilt);
   }

   public BlockFace getFacing() {
      return (BlockFace)this.get(FACING);
   }

   public void setFacing(BlockFace facing) {
      this.set(FACING, facing);
   }

   public Set<BlockFace> getFaces() {
      return this.getValues(FACING);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
