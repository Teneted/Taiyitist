package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftCaveVinesPlant extends CraftBlockData implements CaveVinesPlant {
   private static final BooleanProperty BERRIES = getBoolean(CaveVinesPlantBlock.class, "berries");

   public CraftCaveVinesPlant() {
   }

   public CraftCaveVinesPlant(BlockState state) {
      super(state);
   }

   public boolean isBerries() {
      return (Boolean)this.get(BERRIES);
   }

   public void setBerries(boolean berries) {
      this.set(BERRIES, berries);
   }
}
