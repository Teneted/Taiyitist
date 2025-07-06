package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftGate extends CraftBlockData implements Gate {
   private static final BooleanProperty IN_WALL = getBoolean("in_wall");

   public boolean isInWall() {
      return (Boolean)this.get(IN_WALL);
   }

   public void setInWall(boolean inWall) {
      this.set(IN_WALL, inWall);
   }
}
