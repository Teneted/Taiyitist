package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftSapling extends CraftBlockData implements Sapling {
   private static final IntegerProperty STAGE = getInteger(SaplingBlock.class, "stage");

   public CraftSapling() {
   }

   public CraftSapling(BlockState state) {
      super(state);
   }

   public int getStage() {
      return (Integer)this.get(STAGE);
   }

   public void setStage(int stage) {
      this.set(STAGE, stage);
   }

   public int getMaximumStage() {
      return getMax(STAGE);
   }
}
