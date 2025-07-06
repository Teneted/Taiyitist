package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Hangable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.MangrovePropagule;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftMangrovePropagule extends CraftBlockData implements MangrovePropagule, Ageable, Hangable, Sapling, Waterlogged {
   private static final IntegerProperty AGE = getInteger(MangrovePropaguleBlock.class, "age");
   private static final BooleanProperty HANGING = getBoolean(MangrovePropaguleBlock.class, "hanging");
   private static final IntegerProperty STAGE = getInteger(MangrovePropaguleBlock.class, "stage");
   private static final BooleanProperty WATERLOGGED = getBoolean(MangrovePropaguleBlock.class, "waterlogged");

   public CraftMangrovePropagule() {
   }

   public CraftMangrovePropagule(BlockState state) {
      super(state);
   }

   public int getAge() {
      return (Integer)this.get(AGE);
   }

   public void setAge(int age) {
      this.set(AGE, age);
   }

   public int getMaximumAge() {
      return getMax(AGE);
   }

   public boolean isHanging() {
      return (Boolean)this.get(HANGING);
   }

   public void setHanging(boolean hanging) {
      this.set(HANGING, hanging);
   }

   public int getStage() {
      return (Integer)this.get(STAGE);
   }

   public void setStage(int stage) {
      this.set(STAGE, stage);
   }

   public int getMaximumStage() {
      return getMax(STAGE);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
