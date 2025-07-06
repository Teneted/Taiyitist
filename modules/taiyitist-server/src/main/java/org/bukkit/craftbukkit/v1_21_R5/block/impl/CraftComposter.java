package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Levelled;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftComposter extends CraftBlockData implements Levelled {
   private static final IntegerProperty LEVEL = getInteger(ComposterBlock.class, "level");

   public CraftComposter() {
   }

   public CraftComposter(BlockState state) {
      super(state);
   }

   public int getLevel() {
      return (Integer)this.get(LEVEL);
   }

   public void setLevel(int level) {
      this.set(LEVEL, level);
   }

   public int getMaximumLevel() {
      return getMax(LEVEL);
   }
}
