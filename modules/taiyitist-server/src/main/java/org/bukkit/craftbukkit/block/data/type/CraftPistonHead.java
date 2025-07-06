package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftPistonHead extends CraftBlockData implements PistonHead {
   private static final BooleanProperty SHORT = getBoolean("short");

   public boolean isShort() {
      return (Boolean)this.get(SHORT);
   }

   public void setShort(boolean _short) {
      this.set(SHORT, _short);
   }
}
