package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.CoralWallFan;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftCoralFanWall extends CraftBlockData implements CoralWallFan, Directional, Waterlogged {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(CoralWallFanBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(CoralWallFanBlock.class, "waterlogged");

   public CraftCoralFanWall() {
   }

   public CraftCoralFanWall(BlockState state) {
      super(state);
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
