package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Powerable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftPressurePlateBinary extends CraftBlockData implements Powerable {
   private static final BooleanProperty POWERED = getBoolean(PressurePlateBlock.class, "powered");

   public CraftPressurePlateBinary() {
   }

   public CraftPressurePlateBinary(BlockState state) {
      super(state);
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
