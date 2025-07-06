package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Snow;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftSnow extends CraftBlockData implements Snow {
   private static final IntegerProperty LAYERS = getInteger(SnowLayerBlock.class, "layers");

   public CraftSnow() {
   }

   public CraftSnow(BlockState state) {
      super(state);
   }

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
