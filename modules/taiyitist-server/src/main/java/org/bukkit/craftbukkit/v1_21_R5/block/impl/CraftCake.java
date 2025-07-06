package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Cake;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftCake extends CraftBlockData implements Cake {
   private static final IntegerProperty BITES = getInteger(CakeBlock.class, "bites");

   public CraftCake() {
   }

   public CraftCake(BlockState state) {
      super(state);
   }

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
