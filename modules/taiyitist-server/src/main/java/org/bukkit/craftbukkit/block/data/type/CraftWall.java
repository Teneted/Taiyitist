package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Wall;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftWall extends CraftBlockData implements Wall {
   private static final BooleanProperty UP = getBoolean("up");
   private static final CraftBlockStateEnum<?, Wall.Height>[] HEIGHTS = new CraftBlockStateEnum[]{getEnum("north", Wall.Height.class), getEnum("east", Wall.Height.class), getEnum("south", Wall.Height.class), getEnum("west", Wall.Height.class)};

   public boolean isUp() {
      return (Boolean)this.get(UP);
   }

   public void setUp(boolean up) {
      this.set(UP, up);
   }

   public Wall.Height getHeight(BlockFace face) {
      return (Wall.Height)this.get(HEIGHTS[face.ordinal()]);
   }

   public void setHeight(BlockFace face, Wall.Height height) {
      this.set(HEIGHTS[face.ordinal()], height);
   }
}
