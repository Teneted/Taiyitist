package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftDaylightDetector extends CraftBlockData implements DaylightDetector {
   private static final BooleanProperty INVERTED = getBoolean("inverted");

   public boolean isInverted() {
      return (Boolean)this.get(INVERTED);
   }

   public void setInverted(boolean inverted) {
      this.set(INVERTED, inverted);
   }
}
