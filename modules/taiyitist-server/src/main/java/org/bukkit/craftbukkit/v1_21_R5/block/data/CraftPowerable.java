package org.bukkit.craftbukkit.v1_21_R5.block.data;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Powerable;

public abstract class CraftPowerable extends CraftBlockData implements Powerable {
   private static final BooleanProperty POWERED = getBoolean("powered");

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
