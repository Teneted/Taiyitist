package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Snow;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public class CraftSnow extends CraftBlockData implements Snow {
   private static final IntegerProperty LAYERS = getInteger("layers");

   public int getLayers() {
      return (Integer)this.get(LAYERS);
   }

   public void setLayers(int layers) {
      this.set(LAYERS, layers);
   }

   public int getMinimumLayers() {
      return getMin(LAYERS);
   }

   public int getMaximumLayers() {
      return getMax(LAYERS);
   }
}
