package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftSculkShrieker extends CraftBlockData implements SculkShrieker, Waterlogged {
   private static final BooleanProperty CAN_SUMMON = getBoolean(SculkShriekerBlock.class, "can_summon");
   private static final BooleanProperty SHRIEKING = getBoolean(SculkShriekerBlock.class, "shrieking");
   private static final BooleanProperty WATERLOGGED = getBoolean(SculkShriekerBlock.class, "waterlogged");

   public CraftSculkShrieker() {
   }

   public CraftSculkShrieker(BlockState state) {
      super(state);
   }

   public boolean isCanSummon() {
      return (Boolean)this.get(CAN_SUMMON);
   }

   public void setCanSummon(boolean can_summon) {
      this.set(CAN_SUMMON, can_summon);
   }

   public boolean isShrieking() {
      return (Boolean)this.get(SHRIEKING);
   }

   public void setShrieking(boolean shrieking) {
      this.set(SHRIEKING, shrieking);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
