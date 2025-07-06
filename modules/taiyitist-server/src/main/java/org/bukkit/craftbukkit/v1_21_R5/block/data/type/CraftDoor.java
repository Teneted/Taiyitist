package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftDoor extends CraftBlockData implements Door {
   private static final CraftBlockStateEnum<?, Door.Hinge> HINGE = getEnum("hinge", Door.Hinge.class);

   public Door.Hinge getHinge() {
      return (Door.Hinge)this.get(HINGE);
   }

   public void setHinge(Door.Hinge hinge) {
      this.set(HINGE, hinge);
   }
}
