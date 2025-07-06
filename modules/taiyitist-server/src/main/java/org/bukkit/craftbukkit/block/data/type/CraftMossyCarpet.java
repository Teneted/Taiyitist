package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.MossyCarpet;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftMossyCarpet extends CraftBlockData implements MossyCarpet {
   private static final BooleanProperty BOTTOM = getBoolean("bottom");
   private static final CraftBlockStateEnum<?, MossyCarpet.Height>[] HEIGHTS = new CraftBlockStateEnum[]{getEnum("north", MossyCarpet.Height.class), getEnum("east", MossyCarpet.Height.class), getEnum("south", MossyCarpet.Height.class), getEnum("west", MossyCarpet.Height.class)};

   public boolean isBottom() {
      return (Boolean)this.get(BOTTOM);
   }

   public void setBottom(boolean up) {
      this.set(BOTTOM, up);
   }

   public MossyCarpet.Height getHeight(BlockFace face) {
      return (MossyCarpet.Height)this.get(HEIGHTS[face.ordinal()]);
   }

   public void setHeight(BlockFace face, MossyCarpet.Height height) {
      this.set(HEIGHTS[face.ordinal()], height);
   }
}
