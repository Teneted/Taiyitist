package org.bukkit.craftbukkit.block.data;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Snowable;

public abstract class CraftSnowable extends CraftBlockData implements Snowable {
   private static final BooleanProperty SNOWY = getBoolean("snowy");

   public boolean isSnowy() {
      return (Boolean)this.get(SNOWY);
   }

   public void setSnowy(boolean snowy) {
      this.set(SNOWY, snowy);
   }
}
