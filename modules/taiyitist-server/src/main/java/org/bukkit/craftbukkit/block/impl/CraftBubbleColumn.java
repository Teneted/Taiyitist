package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftBubbleColumn extends CraftBlockData implements BubbleColumn {
   private static final BooleanProperty DRAG = getBoolean(BubbleColumnBlock.class, "drag");

   public CraftBubbleColumn() {
   }

   public CraftBubbleColumn(BlockState state) {
      super(state);
   }

   public boolean isDrag() {
      return (Boolean)this.get(DRAG);
   }

   public void setDrag(boolean drag) {
      this.set(DRAG, drag);
   }
}
