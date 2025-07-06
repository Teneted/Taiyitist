package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftTrialSpawner extends CraftBlockData implements TrialSpawner {
   private static final CraftBlockStateEnum<?, TrialSpawner.State> TRIAL_SPAWNER_STATE = getEnum("trial_spawner_state", TrialSpawner.State.class);
   private static final BooleanProperty OMINOUS = getBoolean("ominous");

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
