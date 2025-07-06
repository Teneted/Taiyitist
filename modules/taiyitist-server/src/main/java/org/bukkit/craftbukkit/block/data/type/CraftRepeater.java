package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftRepeater extends CraftBlockData implements Repeater {
   private static final IntegerProperty DELAY = getInteger("delay");
   private static final BooleanProperty LOCKED = getBoolean("locked");

   public int getDelay() {
      return (Integer)this.get(DELAY);
   }

   public void setDelay(int delay) {
      this.set(DELAY, delay);
   }

   public int getMinimumDelay() {
      return getMin(DELAY);
   }

   public int getMaximumDelay() {
      return getMax(DELAY);
   }

   public boolean isLocked() {
      return (Boolean)this.get(LOCKED);
   }

   public void setLocked(boolean locked) {
      this.set(LOCKED, locked);
   }
}
