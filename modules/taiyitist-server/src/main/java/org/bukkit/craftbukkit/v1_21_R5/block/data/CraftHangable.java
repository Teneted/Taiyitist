package org.bukkit.craftbukkit.v1_21_R5.block.data;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Hangable;

public abstract class CraftHangable extends CraftBlockData implements Hangable {
   private static final BooleanProperty HANGING = getBoolean("hanging");

   public boolean isHanging() {
      return (Boolean)this.get(HANGING);
   }

   public void setHanging(boolean hanging) {
      this.set(HANGING, hanging);
   }
}
