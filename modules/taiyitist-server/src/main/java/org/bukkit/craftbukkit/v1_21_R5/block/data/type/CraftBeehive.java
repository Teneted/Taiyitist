package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftBeehive extends CraftBlockData implements Beehive {
   private static final IntegerProperty HONEY_LEVEL = getInteger("honey_level");

   public int getHoneyLevel() {
      return (Integer)this.get(HONEY_LEVEL);
   }

   public void setHoneyLevel(int honeyLevel) {
      this.set(HONEY_LEVEL, honeyLevel);
   }

   public int getMaximumHoneyLevel() {
      return getMax(HONEY_LEVEL);
   }
}
