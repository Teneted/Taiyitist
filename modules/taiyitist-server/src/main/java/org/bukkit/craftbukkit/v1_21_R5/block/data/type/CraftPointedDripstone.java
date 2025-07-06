package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import java.util.Set;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftPointedDripstone extends CraftBlockData implements PointedDripstone {
   private static final CraftBlockStateEnum<?, BlockFace> VERTICAL_DIRECTION = getEnum("vertical_direction", BlockFace.class);
   private static final CraftBlockStateEnum<?, PointedDripstone.Thickness> THICKNESS = getEnum("thickness", PointedDripstone.Thickness.class);

   public BlockFace getVerticalDirection() {
      return (BlockFace)this.get(VERTICAL_DIRECTION);
   }

   public void setVerticalDirection(BlockFace direction) {
      this.set(VERTICAL_DIRECTION, direction);
   }

   public Set<BlockFace> getVerticalDirections() {
      return this.getValues(VERTICAL_DIRECTION);
   }

   public PointedDripstone.Thickness getThickness() {
      return (PointedDripstone.Thickness)this.get(THICKNESS);
   }

   public void setThickness(PointedDripstone.Thickness thickness) {
      this.set(THICKNESS, thickness);
   }
}
