package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftPiston extends CraftBlockData implements Piston {
   private static final BooleanProperty EXTENDED = getBoolean("extended");

   public boolean isExtended() {
      return (Boolean)this.get(EXTENDED);
   }

   public void setExtended(boolean extended) {
      this.set(EXTENDED, extended);
   }
}
