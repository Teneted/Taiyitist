package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.SculkCatalyst;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftSculkCatalyst extends CraftBlockData implements SculkCatalyst {
   private static final BooleanProperty BLOOM = getBoolean(SculkCatalystBlock.class, "bloom");

   public CraftSculkCatalyst() {
   }

   public CraftSculkCatalyst(BlockState state) {
      super(state);
   }

   public boolean isBloom() {
      return (Boolean)this.get(BLOOM);
   }

   public void setBloom(boolean bloom) {
      this.set(BLOOM, bloom);
   }
}
