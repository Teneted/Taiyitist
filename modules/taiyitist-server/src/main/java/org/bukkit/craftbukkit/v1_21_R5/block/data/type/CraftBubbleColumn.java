package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftBubbleColumn extends CraftBlockData implements BubbleColumn {
   private static final BooleanProperty DRAG = getBoolean("drag");

   public boolean isDrag() {
      return (Boolean)this.get(DRAG);
   }

   public void setDrag(boolean drag) {
      this.set(DRAG, drag);
   }
}
