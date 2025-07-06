package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftCampfire extends CraftBlockData implements Campfire {
   private static final BooleanProperty SIGNAL_FIRE = getBoolean("signal_fire");

   public boolean isSignalFire() {
      return (Boolean)this.get(SIGNAL_FIRE);
   }

   public void setSignalFire(boolean signalFire) {
      this.set(SIGNAL_FIRE, signalFire);
   }
}
