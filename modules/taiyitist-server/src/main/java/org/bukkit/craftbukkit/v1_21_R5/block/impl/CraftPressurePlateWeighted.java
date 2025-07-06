package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftPressurePlateWeighted extends CraftBlockData implements AnaloguePowerable {
   private static final IntegerProperty POWER = getInteger(WeightedPressurePlateBlock.class, "power");

   public CraftPressurePlateWeighted() {
   }

   public CraftPressurePlateWeighted(BlockState state) {
      super(state);
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
}
