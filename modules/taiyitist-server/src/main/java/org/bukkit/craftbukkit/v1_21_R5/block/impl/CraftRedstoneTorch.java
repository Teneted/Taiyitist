package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Lightable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftRedstoneTorch extends CraftBlockData implements Lightable {
   private static final BooleanProperty LIT = getBoolean(RedstoneTorchBlock.class, "lit");

   public CraftRedstoneTorch() {
   }

   public CraftRedstoneTorch(BlockState state) {
      super(state);
   }

   public boolean isLit() {
      return (Boolean)this.get(LIT);
   }

   public void setLit(boolean lit) {
      this.set(LIT, lit);
   }
}
