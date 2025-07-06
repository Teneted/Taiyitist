package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Snowable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftGrass extends CraftBlockData implements Snowable {
   private static final BooleanProperty SNOWY = getBoolean(GrassBlock.class, "snowy");

   public CraftGrass() {
   }

   public CraftGrass(BlockState state) {
      super(state);
   }

   public boolean isSnowy() {
      return (Boolean)this.get(SNOWY);
   }

   public void setSnowy(boolean snowy) {
      this.set(SNOWY, snowy);
   }
}
