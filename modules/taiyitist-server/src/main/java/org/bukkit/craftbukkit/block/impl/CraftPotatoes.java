package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftPotatoes extends CraftBlockData implements Ageable {
   private static final IntegerProperty AGE = getInteger(PotatoBlock.class, "age");

   public CraftPotatoes() {
   }

   public CraftPotatoes(BlockState state) {
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
