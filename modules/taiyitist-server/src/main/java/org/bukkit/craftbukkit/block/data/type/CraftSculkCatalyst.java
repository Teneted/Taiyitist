package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.SculkCatalyst;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftSculkCatalyst extends CraftBlockData implements SculkCatalyst {
   private static final BooleanProperty BLOOM = getBoolean("bloom");

   public boolean isBloom() {
      return (Boolean)this.get(BLOOM);
   }

   public void setBloom(boolean bloom) {
      this.set(BLOOM, bloom);
   }
}
