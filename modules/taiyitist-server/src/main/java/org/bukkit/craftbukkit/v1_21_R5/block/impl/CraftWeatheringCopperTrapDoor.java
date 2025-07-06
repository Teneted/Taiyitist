package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.WeatheringCopperTrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftWeatheringCopperTrapDoor extends CraftBlockData implements TrapDoor, Bisected, Directional, Openable, Powerable, Waterlogged {
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(WeatheringCopperTrapDoorBlock.class, "half", Bisected.Half.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(WeatheringCopperTrapDoorBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty OPEN = getBoolean(WeatheringCopperTrapDoorBlock.class, "open");
   private static final BooleanProperty POWERED = getBoolean(WeatheringCopperTrapDoorBlock.class, "powered");
   private static final BooleanProperty WATERLOGGED = getBoolean(WeatheringCopperTrapDoorBlock.class, "waterlogged");

   public CraftWeatheringCopperTrapDoor() {
   }

   public CraftWeatheringCopperTrapDoor(BlockState state) {
      super(state);
   }

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
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

   public boolean isOpen() {
      return (Boolean)this.get(OPEN);
   }

   public void setOpen(boolean open) {
      this.set(OPEN, open);
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
