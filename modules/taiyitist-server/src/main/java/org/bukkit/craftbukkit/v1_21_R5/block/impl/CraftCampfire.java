package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftCampfire extends CraftBlockData implements Campfire, Directional, Lightable, Waterlogged {
   private static final BooleanProperty SIGNAL_FIRE = getBoolean(CampfireBlock.class, "signal_fire");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(CampfireBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty LIT = getBoolean(CampfireBlock.class, "lit");
   private static final BooleanProperty WATERLOGGED = getBoolean(CampfireBlock.class, "waterlogged");

   public CraftCampfire() {
   }

   public CraftCampfire(BlockState state) {
      super(state);
   }

   public boolean isSignalFire() {
      return (Boolean)this.get(SIGNAL_FIRE);
   }

   public void setSignalFire(boolean signalFire) {
      this.set(SIGNAL_FIRE, signalFire);
   }

   public BlockFace getFacing() {
      return (BlockFace)this.get(FACING);
   }

   public void setFacing(BlockFace facing) {
      this.set(FACING, facing);
   }

   public Set<BlockFace> getFaces() {
      return this.getValues(FACING);
   }

   public boolean isLit() {
      return (Boolean)this.get(LIT);
   }

   public void setLit(boolean lit) {
      this.set(LIT, lit);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
