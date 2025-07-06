package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.CalibratedSculkSensor;
import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftCalibratedSculkSensor extends CraftBlockData implements CalibratedSculkSensor, Directional, SculkSensor, AnaloguePowerable, Waterlogged {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(CalibratedSculkSensorBlock.class, "facing", BlockFace.class);
   private static final CraftBlockStateEnum<?, SculkSensor.Phase> PHASE = getEnum(CalibratedSculkSensorBlock.class, "sculk_sensor_phase", SculkSensor.Phase.class);
   private static final IntegerProperty POWER = getInteger(CalibratedSculkSensorBlock.class, "power");
   private static final BooleanProperty WATERLOGGED = getBoolean(CalibratedSculkSensorBlock.class, "waterlogged");

   public CraftCalibratedSculkSensor() {
   }

   public CraftCalibratedSculkSensor(BlockState state) {
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

   public SculkSensor.Phase getPhase() {
      return (SculkSensor.Phase)this.get(PHASE);
   }

   public void setPhase(SculkSensor.Phase phase) {
      this.set(PHASE, phase);
   }

   public int getPower() {
      return (Integer)this.get(POWER);
   }

   public void setPower(int power) {
      this.set(POWER, power);
   }

   public int getMaximumPower() {
      return getMax(POWER);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
