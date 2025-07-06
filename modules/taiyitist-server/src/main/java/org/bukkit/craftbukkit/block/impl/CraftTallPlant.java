package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftTallPlant extends CraftBlockData implements Bisected {
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(DoublePlantBlock.class, "half", Bisected.Half.class);

   public CraftTallPlant() {
   }

   public CraftTallPlant(BlockState state) {
      super(state);
   }

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
   }
}
