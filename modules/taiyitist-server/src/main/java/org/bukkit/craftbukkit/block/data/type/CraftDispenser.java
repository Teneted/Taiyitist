package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftDispenser extends CraftBlockData implements Dispenser {
   private static final BooleanProperty TRIGGERED = getBoolean("triggered");

   public boolean isTriggered() {
      return (Boolean)this.get(TRIGGERED);
   }

   public void setTriggered(boolean triggered) {
      this.set(TRIGGERED, triggered);
   }
}
