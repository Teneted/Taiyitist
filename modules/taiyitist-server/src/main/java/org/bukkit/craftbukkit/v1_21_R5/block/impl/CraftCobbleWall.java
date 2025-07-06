package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Wall;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftCobbleWall extends CraftBlockData implements Wall, Waterlogged {
   private static final BooleanProperty UP = getBoolean(WallBlock.class, "up");
   private static final CraftBlockStateEnum<?, Wall.Height>[] HEIGHTS = new CraftBlockStateEnum[]{getEnum(WallBlock.class, "north", Wall.Height.class), getEnum(WallBlock.class, "east", Wall.Height.class), getEnum(WallBlock.class, "south", Wall.Height.class), getEnum(WallBlock.class, "west", Wall.Height.class)};
   private static final BooleanProperty WATERLOGGED = getBoolean(WallBlock.class, "waterlogged");

   public CraftCobbleWall() {
   }

   public CraftCobbleWall(BlockState state) {
      super(state);
   }

   public boolean isUp() {
      return (Boolean)this.get(UP);
   }

   public void setUp(boolean up) {
      this.set(UP, up);
   }

   public Wall.Height getHeight(BlockFace face) {
      return (Wall.Height)this.get(HEIGHTS[face.ordinal()]);
   }

   public void setHeight(BlockFace face, Wall.Height height) {
      this.set(HEIGHTS[face.ordinal()], height);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
