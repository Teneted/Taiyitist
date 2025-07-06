package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.SculkCatalyst;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftSculkCatalyst extends CraftBlockData implements SculkCatalyst {
   private static final BooleanProperty BLOOM = getBoolean("bloom");

   public boolean isBloom() {
      return (Boolean)this.get(BLOOM);
   }

   public void setBloom(boolean bloom) {
      this.set(BLOOM, bloom);
   }
}
