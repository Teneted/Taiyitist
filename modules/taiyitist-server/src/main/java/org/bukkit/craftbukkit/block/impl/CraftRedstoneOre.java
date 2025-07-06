package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Lightable;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftRedstoneOre extends CraftBlockData implements Lightable {
   private static final BooleanProperty LIT = getBoolean(RedStoneOreBlock.class, "lit");

   public CraftRedstoneOre() {
   }

   public CraftRedstoneOre(BlockState state) {
      super(state);
   }

   public boolean isLit() {
      return (Boolean)this.get(LIT);
   }

   public void setLit(boolean lit) {
      this.set(LIT, lit);
   }
}
