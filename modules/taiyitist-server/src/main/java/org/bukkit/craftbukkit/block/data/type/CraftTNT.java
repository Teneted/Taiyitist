package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.TNT;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftTNT extends CraftBlockData implements TNT {
   private static final BooleanProperty UNSTABLE = getBoolean("unstable");

   public boolean isUnstable() {
      return (Boolean)this.get(UNSTABLE);
   }

   public void setUnstable(boolean unstable) {
      this.set(UNSTABLE, unstable);
   }
}
