package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftMinecartDetector extends CraftBlockData implements RedstoneRail, Powerable, Rail, Waterlogged {
   private static final BooleanProperty POWERED = getBoolean(DetectorRailBlock.class, "powered");
   private static final CraftBlockStateEnum<?, Rail.Shape> SHAPE = getEnum(DetectorRailBlock.class, "shape", Rail.Shape.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(DetectorRailBlock.class, "waterlogged");

   public CraftMinecartDetector() {
   }

   public CraftMinecartDetector(BlockState state) {
      super(state);
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
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
