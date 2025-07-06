package org.bukkit.craftbukkit.block.data;

import java.util.Set;
import org.bukkit.block.data.Rail;

public abstract class CraftRail extends CraftBlockData implements Rail {
   private static final CraftBlockStateEnum<?, Rail.Shape> SHAPE = getEnum("shape", Rail.Shape.class);

   public Rail.Shape getShape() {
      return (Rail.Shape)this.get(SHAPE);
   }

   public void setShape(Rail.Shape shape) {
      this.set(SHAPE, shape);
   }

   public Set<Rail.Shape> getShapes() {
      return this.getValues(SHAPE);
   }
}
