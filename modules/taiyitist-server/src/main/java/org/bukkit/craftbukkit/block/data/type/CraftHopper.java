package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftHopper extends CraftBlockData implements Hopper {
   private static final BooleanProperty ENABLED = getBoolean("enabled");

   public boolean isEnabled() {
      return (Boolean)this.get(ENABLED);
   }

   public void setEnabled(boolean enabled) {
      this.set(ENABLED, enabled);
   }
}
