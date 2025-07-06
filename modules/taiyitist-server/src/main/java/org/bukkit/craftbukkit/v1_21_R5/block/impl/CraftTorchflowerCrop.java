package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.TorchflowerCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftTorchflowerCrop extends CraftBlockData implements Ageable {
   private static final IntegerProperty AGE = getInteger(TorchflowerCropBlock.class, "age");

   public CraftTorchflowerCrop() {
   }

   public CraftTorchflowerCrop(BlockState state) {
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
}
