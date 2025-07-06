package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.DriedGhast;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftDriedGhast extends CraftBlockData implements DriedGhast, Directional, Waterlogged {
   private static final IntegerProperty HYDRATION = getInteger(DriedGhastBlock.class, "hydration");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(DriedGhastBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(DriedGhastBlock.class, "waterlogged");

   public CraftDriedGhast() {
   }

   public CraftDriedGhast(BlockState state) {
      super(state);
   }

   public int getHydration() {
      return (Integer)this.get(HYDRATION);
   }

   public void setHydration(int hydration) {
      this.set(HYDRATION, hydration);
   }

   public int getMaximumHydration() {
      return getMax(HYDRATION);
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

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
