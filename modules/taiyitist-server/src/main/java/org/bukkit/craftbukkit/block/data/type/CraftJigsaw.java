package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftJigsaw extends CraftBlockData implements Jigsaw {
   private static final CraftBlockStateEnum<?, Jigsaw.Orientation> ORIENTATION = getEnum("orientation", Jigsaw.Orientation.class);

   public Jigsaw.Orientation getOrientation() {
      return (Jigsaw.Orientation)this.get(ORIENTATION);
   }

   public void setOrientation(Jigsaw.Orientation orientation) {
      this.set(ORIENTATION, orientation);
   }
}
