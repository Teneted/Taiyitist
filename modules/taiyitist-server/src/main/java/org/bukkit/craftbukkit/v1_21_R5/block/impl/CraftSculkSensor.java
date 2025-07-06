package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftSculkSensor extends CraftBlockData implements SculkSensor, AnaloguePowerable, Waterlogged {
   private static final CraftBlockStateEnum<?, SculkSensor.Phase> PHASE = getEnum(SculkSensorBlock.class, "sculk_sensor_phase", SculkSensor.Phase.class);
   private static final IntegerProperty POWER = getInteger(SculkSensorBlock.class, "power");
   private static final BooleanProperty WATERLOGGED = getBoolean(SculkSensorBlock.class, "waterlogged");

   public CraftSculkSensor() {
   }

   public CraftSculkSensor(BlockState state) {
      super(state);
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
