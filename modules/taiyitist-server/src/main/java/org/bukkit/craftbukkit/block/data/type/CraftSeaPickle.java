package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftSeaPickle extends CraftBlockData implements SeaPickle {
   private static final IntegerProperty PICKLES = getInteger("pickles");

   public int getPickles() {
      return (Integer)this.get(PICKLES);
   }

   public void setPickles(int pickles) {
      this.set(PICKLES, pickles);
   }

   public int getMinimumPickles() {
      return getMin(PICKLES);
   }

   public int getMaximumPickles() {
      return getMax(PICKLES);
   }
}
