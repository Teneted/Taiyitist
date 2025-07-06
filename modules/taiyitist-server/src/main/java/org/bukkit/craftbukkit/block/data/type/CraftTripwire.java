package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftTripwire extends CraftBlockData implements Tripwire {
   private static final BooleanProperty DISARMED = getBoolean("disarmed");

   public boolean isDisarmed() {
      return (Boolean)this.get(DISARMED);
   }

   public void setDisarmed(boolean disarmed) {
      this.set(DISARMED, disarmed);
   }
}
