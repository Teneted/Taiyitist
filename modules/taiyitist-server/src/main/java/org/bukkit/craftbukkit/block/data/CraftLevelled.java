package org.bukkit.craftbukkit.block.data;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Levelled;

public abstract class CraftLevelled extends CraftBlockData implements Levelled {
   private static final IntegerProperty LEVEL = getInteger("level");

   public int getLevel() {
      return (Integer)this.get(LEVEL);
   }

   public void setLevel(int level) {
      this.set(LEVEL, level);
   }

   public int getMaximumLevel() {
      return getMax(LEVEL);
   }
}
