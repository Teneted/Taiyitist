package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.CreakingHeart;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftCreakingHeart extends CraftBlockData implements CreakingHeart, Orientable {
   private static final CraftBlockStateEnum<?, CreakingHeart.State> CREAKING_HEART_STATE = getEnum(CreakingHeartBlock.class, "creaking_heart_state", CreakingHeart.State.class);
   private static final BooleanProperty NATURAL = getBoolean(CreakingHeartBlock.class, "natural");
   private static final CraftBlockStateEnum<?, Axis> AXIS = getEnum(CreakingHeartBlock.class, "axis", Axis.class);

   public CraftCreakingHeart() {
   }

   public CraftCreakingHeart(BlockState state) {
      super(state);
   }

   public boolean isActive() {
      return this.getCreakingHeartState() == State.AWAKE;
   }

   public void setActive(boolean active) {
      this.setCreakingHeartState(State.AWAKE);
   }

   public boolean isNatural() {
      return (Boolean)this.get(NATURAL);
   }

   public void setNatural(boolean natural) {
      this.set(NATURAL, natural);
   }

   public CreakingHeart.State getCreakingHeartState() {
      return (CreakingHeart.State)this.get(CREAKING_HEART_STATE);
   }

   public void setCreakingHeartState(CreakingHeart.State state) {
      this.set(CREAKING_HEART_STATE, state);
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
