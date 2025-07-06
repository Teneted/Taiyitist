package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Scaffolding;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftScaffolding extends CraftBlockData implements Scaffolding {
   private static final BooleanProperty BOTTOM = getBoolean("bottom");
   private static final IntegerProperty DISTANCE = getInteger("distance");

   public boolean isBottom() {
      return (Boolean)this.get(BOTTOM);
   }

   public void setBottom(boolean bottom) {
      this.set(BOTTOM, bottom);
   }

   public int getDistance() {
      return (Integer)this.get(DISTANCE);
   }

   public void setDistance(int distance) {
      this.set(DISTANCE, distance);
   }

   public int getMaximumDistance() {
      return getMax(DISTANCE);
   }
}
