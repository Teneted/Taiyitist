package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Hatchable;
import org.bukkit.block.data.type.TurtleEgg;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftTurtleEgg extends CraftBlockData implements TurtleEgg, Hatchable {
   private static final IntegerProperty EGGS = getInteger(TurtleEggBlock.class, "eggs");
   private static final IntegerProperty HATCH = getInteger(TurtleEggBlock.class, "hatch");

   public CraftTurtleEgg() {
   }

   public CraftTurtleEgg(BlockState state) {
      super(state);
   }

   public int getEggs() {
      return (Integer)this.get(EGGS);
   }

   public void setEggs(int eggs) {
      this.set(EGGS, eggs);
   }

   public int getMinimumEggs() {
      return getMin(EGGS);
   }

   public int getMaximumEggs() {
      return getMax(EGGS);
   }

   public int getHatch() {
      return (Integer)this.get(HATCH);
   }

   public void setHatch(int hatch) {
      this.set(HATCH, hatch);
   }

   public int getMaximumHatch() {
      return getMax(HATCH);
   }
}
