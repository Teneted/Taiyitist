package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftRotatable extends CraftBlockData implements Orientable {
   private static final CraftBlockStateEnum<?, Axis> AXIS = getEnum(RotatedPillarBlock.class, "axis", Axis.class);

   public CraftRotatable() {
   }

   public CraftRotatable(BlockState state) {
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
}
