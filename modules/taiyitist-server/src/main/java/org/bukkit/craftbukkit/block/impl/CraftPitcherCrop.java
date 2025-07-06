package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.PitcherCrop;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftPitcherCrop extends CraftBlockData implements PitcherCrop, Ageable, Bisected {
   private static final IntegerProperty AGE = getInteger(PitcherCropBlock.class, "age");
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(PitcherCropBlock.class, "half", Bisected.Half.class);

   public CraftPitcherCrop() {
   }

   public CraftPitcherCrop(BlockState state) {
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

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
   }
}
