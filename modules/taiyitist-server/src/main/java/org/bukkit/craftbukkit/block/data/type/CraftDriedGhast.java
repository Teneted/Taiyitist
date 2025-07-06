package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.DriedGhast;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftDriedGhast extends CraftBlockData implements DriedGhast {
   private static final IntegerProperty HYDRATION = getInteger("hydration");

   public int getHydration() {
      return (Integer)this.get(HYDRATION);
   }

   public void setHydration(int hydration) {
      this.set(HYDRATION, hydration);
   }

   public int getMaximumHydration() {
      return getMax(HYDRATION);
   }
}
