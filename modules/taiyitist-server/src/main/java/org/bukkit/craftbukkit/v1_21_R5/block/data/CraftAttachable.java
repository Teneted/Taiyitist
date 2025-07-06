package org.bukkit.craftbukkit.v1_21_R5.block.data;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Attachable;

public abstract class CraftAttachable extends CraftBlockData implements Attachable {
   private static final BooleanProperty ATTACHED = getBoolean("attached");

   public boolean isAttached() {
      return (Boolean)this.get(ATTACHED);
   }

   public void setAttached(boolean attached) {
      this.set(ATTACHED, attached);
   }
}
