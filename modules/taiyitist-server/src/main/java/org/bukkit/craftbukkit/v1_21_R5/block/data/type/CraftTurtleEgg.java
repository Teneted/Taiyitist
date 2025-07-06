package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.TurtleEgg;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftTurtleEgg extends CraftBlockData implements TurtleEgg {
   private static final IntegerProperty EGGS = getInteger("eggs");

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
}
