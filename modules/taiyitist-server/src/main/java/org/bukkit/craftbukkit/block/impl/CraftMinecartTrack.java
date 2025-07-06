package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftMinecartTrack extends CraftBlockData implements Rail, Waterlogged {
   private static final CraftBlockStateEnum<?, Rail.Shape> SHAPE = getEnum(RailBlock.class, "shape", Rail.Shape.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(RailBlock.class, "waterlogged");

   public CraftMinecartTrack() {
   }

   public CraftMinecartTrack(BlockState state) {
      super(state);
   }

   public Rail.Shape getShape() {
      return (Rail.Shape)this.get(SHAPE);
   }

   public void setShape(Rail.Shape shape) {
      this.set(SHAPE, shape);
   }

   public Set<Rail.Shape> getShapes() {
      return this.getValues(SHAPE);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
