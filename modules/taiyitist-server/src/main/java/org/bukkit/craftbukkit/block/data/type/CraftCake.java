package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Cake;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftCake extends CraftBlockData implements Cake {
   private static final IntegerProperty BITES = getInteger("bites");

   public int getBites() {
      return (Integer)this.get(BITES);
   }

   public void setBites(int bites) {
      this.set(BITES, bites);
   }

   public int getMaximumBites() {
      return getMax(BITES);
   }
}
