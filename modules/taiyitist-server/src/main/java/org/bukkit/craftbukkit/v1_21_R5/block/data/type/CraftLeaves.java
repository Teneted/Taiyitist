package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftLeaves extends CraftBlockData implements Leaves {
   private static final IntegerProperty DISTANCE = getInteger("distance");
   private static final BooleanProperty PERSISTENT = getBoolean("persistent");

   public boolean isPersistent() {
      return (Boolean)this.get(PERSISTENT);
   }

   public void setPersistent(boolean persistent) {
      this.set(PERSISTENT, persistent);
   }

   public int getDistance() {
      return (Integer)this.get(DISTANCE);
   }

   public void setDistance(int distance) {
      this.set(DISTANCE, distance);
   }
}
