package org.bukkit.craftbukkit.block.data;

import java.util.Set;
import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;

public class CraftOrientable extends CraftBlockData implements Orientable {
   private static final CraftBlockStateEnum<?, Axis> AXIS = getEnum("axis", Axis.class);

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
