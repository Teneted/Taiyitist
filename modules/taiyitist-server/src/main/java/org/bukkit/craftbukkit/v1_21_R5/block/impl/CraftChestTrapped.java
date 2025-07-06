package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Chest;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftChestTrapped extends CraftBlockData implements Chest, Directional, Waterlogged {
   private static final CraftBlockStateEnum<?, Chest.Type> TYPE = getEnum(TrappedChestBlock.class, "type", Chest.Type.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(TrappedChestBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(TrappedChestBlock.class, "waterlogged");

   public CraftChestTrapped() {
   }

   public CraftChestTrapped(BlockState state) {
      super(state);
   }

   public Chest.Type getType() {
      return (Chest.Type)this.get(TYPE);
   }

   public void setType(Chest.Type type) {
      this.set(TYPE, type);
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
