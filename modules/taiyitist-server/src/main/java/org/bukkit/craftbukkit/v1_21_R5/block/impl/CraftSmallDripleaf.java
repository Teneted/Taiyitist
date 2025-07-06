package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Dripleaf;
import org.bukkit.block.data.type.SmallDripleaf;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftSmallDripleaf extends CraftBlockData implements SmallDripleaf, Dripleaf, Bisected, Directional, Waterlogged {
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(SmallDripleafBlock.class, "half", Bisected.Half.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(SmallDripleafBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(SmallDripleafBlock.class, "waterlogged");

   public CraftSmallDripleaf() {
   }

   public CraftSmallDripleaf(BlockState state) {
      super(state);
   }

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
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
