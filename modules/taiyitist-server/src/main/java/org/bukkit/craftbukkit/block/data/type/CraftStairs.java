package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftStairs extends CraftBlockData implements Stairs {
   private static final CraftBlockStateEnum<?, Stairs.Shape> SHAPE = getEnum("shape", Stairs.Shape.class);

   public Stairs.Shape getShape() {
      return (Stairs.Shape)this.get(SHAPE);
   }

   public void setShape(Stairs.Shape shape) {
      this.set(SHAPE, shape);
   }
}
