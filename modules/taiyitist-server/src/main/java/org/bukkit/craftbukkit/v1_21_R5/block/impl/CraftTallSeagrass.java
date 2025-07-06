package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftTallSeagrass extends CraftBlockData implements Bisected {
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(TallSeagrassBlock.class, "half", Bisected.Half.class);

   public CraftTallSeagrass() {
   }

   public CraftTallSeagrass(BlockState state) {
      super(state);
   }

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
   }
}
