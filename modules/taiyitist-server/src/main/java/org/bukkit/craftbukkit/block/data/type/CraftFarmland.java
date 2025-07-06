package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftFarmland extends CraftBlockData implements Farmland {
   private static final IntegerProperty MOISTURE = getInteger("moisture");

   public int getMoisture() {
      return (Integer)this.get(MOISTURE);
   }

   public void setMoisture(int moisture) {
      this.set(MOISTURE, moisture);
   }

   public int getMaximumMoisture() {
      return getMax(MOISTURE);
   }
}
