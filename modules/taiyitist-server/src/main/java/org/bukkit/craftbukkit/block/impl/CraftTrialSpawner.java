package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftTrialSpawner extends CraftBlockData implements TrialSpawner {
   private static final CraftBlockStateEnum<?, TrialSpawner.State> TRIAL_SPAWNER_STATE = getEnum(TrialSpawnerBlock.class, "trial_spawner_state", TrialSpawner.State.class);
   private static final BooleanProperty OMINOUS = getBoolean(TrialSpawnerBlock.class, "ominous");

   public CraftTrialSpawner() {
   }

   public CraftTrialSpawner(BlockState state) {
      super(state);
   }

   public TrialSpawner.State getTrialSpawnerState() {
      return (TrialSpawner.State)this.get(TRIAL_SPAWNER_STATE);
   }

   public void setTrialSpawnerState(TrialSpawner.State state) {
      this.set(TRIAL_SPAWNER_STATE, state);
   }

   public boolean isOminous() {
      return (Boolean)this.get(OMINOUS);
   }

   public void setOminous(boolean ominous) {
      this.set(OMINOUS, ominous);
   }
}
