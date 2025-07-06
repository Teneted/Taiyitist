package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Brushable;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftBrushable extends CraftBlockData implements Brushable {
   private static final IntegerProperty DUSTED = getInteger(BrushableBlock.class, "dusted");

   public CraftBrushable() {
   }

   public CraftBrushable(BlockState state) {
      super(state);
   }

   public int getDusted() {
      return (Integer)this.get(DUSTED);
   }

   public void setDusted(int dusted) {
      this.set(DUSTED, dusted);
   }

   public int getMaximumDusted() {
      return getMax(DUSTED);
   }
}
