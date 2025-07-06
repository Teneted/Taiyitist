package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftPointedDripstone extends CraftBlockData implements PointedDripstone, Waterlogged {
   private static final CraftBlockStateEnum<?, BlockFace> VERTICAL_DIRECTION = getEnum(PointedDripstoneBlock.class, "vertical_direction", BlockFace.class);
   private static final CraftBlockStateEnum<?, PointedDripstone.Thickness> THICKNESS = getEnum(PointedDripstoneBlock.class, "thickness", PointedDripstone.Thickness.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(PointedDripstoneBlock.class, "waterlogged");

   public CraftPointedDripstone() {
   }

   public CraftPointedDripstone(BlockState state) {
      super(state);
   }

   public BlockFace getVerticalDirection() {
      return (BlockFace)this.get(VERTICAL_DIRECTION);
   }

   public void setVerticalDirection(BlockFace direction) {
      this.set(VERTICAL_DIRECTION, direction);
   }

   public Set<BlockFace> getVerticalDirections() {
      return this.getValues(VERTICAL_DIRECTION);
   }

   public PointedDripstone.Thickness getThickness() {
      return (PointedDripstone.Thickness)this.get(THICKNESS);
   }

   public void setThickness(PointedDripstone.Thickness thickness) {
      this.set(THICKNESS, thickness);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
