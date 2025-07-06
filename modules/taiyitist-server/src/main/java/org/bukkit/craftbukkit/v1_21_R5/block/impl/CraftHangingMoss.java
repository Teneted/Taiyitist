package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.HangingMoss;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftHangingMoss extends CraftBlockData implements HangingMoss {
   private static final BooleanProperty TIP = getBoolean(HangingMossBlock.class, "tip");

   public CraftHangingMoss() {
   }

   public CraftHangingMoss(BlockState state) {
      super(state);
   }

   public boolean isTip() {
      return (Boolean)this.get(TIP);
   }

   public void setTip(boolean tip) {
      this.set(TIP, tip);
   }
}
