package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftSculkSensor extends CraftBlockData implements SculkSensor {
   private static final CraftBlockStateEnum<?, SculkSensor.Phase> PHASE = getEnum("sculk_sensor_phase", SculkSensor.Phase.class);

   public SculkSensor.Phase getPhase() {
      return (SculkSensor.Phase)this.get(PHASE);
   }

   public void setPhase(SculkSensor.Phase phase) {
      this.set(PHASE, phase);
   }
}
