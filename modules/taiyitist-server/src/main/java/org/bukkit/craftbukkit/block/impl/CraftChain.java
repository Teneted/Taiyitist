package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Chain;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftChain extends CraftBlockData implements Chain, Orientable, Waterlogged {
   private static final CraftBlockStateEnum<?, Axis> AXIS = getEnum(ChainBlock.class, "axis", Axis.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(ChainBlock.class, "waterlogged");

   public CraftChain() {
   }

   public CraftChain(BlockState state) {
      super(state);
   }

   public Axis getAxis() {
      return (Axis)this.get(AXIS);
   }

   public void setAxis(Axis axis) {
      this.set(AXIS, axis);
   }

   public Set<Axis> getAxes() {
      return this.getValues(AXIS);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
