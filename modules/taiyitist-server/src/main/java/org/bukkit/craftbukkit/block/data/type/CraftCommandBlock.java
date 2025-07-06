package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftCommandBlock extends CraftBlockData implements CommandBlock {
   private static final BooleanProperty CONDITIONAL = getBoolean("conditional");

   public boolean isConditional() {
      return (Boolean)this.get(CONDITIONAL);
   }

   public void setConditional(boolean conditional) {
      this.set(CONDITIONAL, conditional);
   }
}
