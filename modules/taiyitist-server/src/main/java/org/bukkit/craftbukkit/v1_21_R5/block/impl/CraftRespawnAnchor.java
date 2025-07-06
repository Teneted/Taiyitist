package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftRespawnAnchor extends CraftBlockData implements RespawnAnchor {
   private static final IntegerProperty CHARGES = getInteger(RespawnAnchorBlock.class, "charges");

   public CraftRespawnAnchor() {
   }

   public CraftRespawnAnchor(BlockState state) {
      super(state);
   }

   public int getCharges() {
      return (Integer)this.get(CHARGES);
   }

   public void setCharges(int charges) {
      this.set(CHARGES, charges);
   }

   public int getMaximumCharges() {
      return getMax(CHARGES);
   }
}
