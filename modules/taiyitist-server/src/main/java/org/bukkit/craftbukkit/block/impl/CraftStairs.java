package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftStairs extends CraftBlockData implements Stairs, Bisected, Directional, Waterlogged {
   private static final CraftBlockStateEnum<?, Stairs.Shape> SHAPE = getEnum(StairBlock.class, "shape", Stairs.Shape.class);
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(StairBlock.class, "half", Bisected.Half.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(StairBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(StairBlock.class, "waterlogged");

   public CraftStairs() {
   }

   public CraftStairs(BlockState state) {
      super(state);
   }

   public Stairs.Shape getShape() {
      return (Stairs.Shape)this.get(SHAPE);
   }

   public void setShape(Stairs.Shape shape) {
      this.set(SHAPE, shape);
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
