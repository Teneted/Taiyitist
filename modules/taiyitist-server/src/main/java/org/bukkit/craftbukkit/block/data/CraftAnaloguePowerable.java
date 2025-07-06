package org.bukkit.craftbukkit.block.data;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.AnaloguePowerable;

public abstract class CraftAnaloguePowerable extends CraftBlockData implements AnaloguePowerable {
   private static final IntegerProperty POWER = getInteger("power");

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
