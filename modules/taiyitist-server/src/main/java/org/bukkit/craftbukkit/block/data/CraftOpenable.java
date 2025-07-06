package org.bukkit.craftbukkit.block.data;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Openable;

public abstract class CraftOpenable extends CraftBlockData implements Openable {
   private static final BooleanProperty OPEN = getBoolean("open");

   public boolean isOpen() {
      return (Boolean)this.get(OPEN);
   }

   public void setOpen(boolean open) {
      this.set(OPEN, open);
   }
}
