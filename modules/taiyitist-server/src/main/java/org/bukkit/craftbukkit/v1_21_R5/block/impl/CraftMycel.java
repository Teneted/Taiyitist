package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Snowable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftMycel extends CraftBlockData implements Snowable {
   private static final BooleanProperty SNOWY = getBoolean(MyceliumBlock.class, "snowy");

   public CraftMycel() {
   }

   public CraftMycel(BlockState state) {
      super(state);
   }

   public boolean isSnowy() {
      return (Boolean)this.get(SNOWY);
   }

   public void setSnowy(boolean snowy) {
      this.set(SNOWY, snowy);
   }
}
