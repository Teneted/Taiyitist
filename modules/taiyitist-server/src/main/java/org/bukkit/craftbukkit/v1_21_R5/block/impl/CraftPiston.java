package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftPiston extends CraftBlockData implements Piston, Directional {
   private static final BooleanProperty EXTENDED = getBoolean(PistonBaseBlock.class, "extended");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(PistonBaseBlock.class, "facing", BlockFace.class);

   public CraftPiston() {
   }

   public CraftPiston(BlockState state) {
      super(state);
   }

   public boolean isExtended() {
      return (Boolean)this.get(EXTENDED);
   }

   public void setExtended(boolean extended) {
      this.set(EXTENDED, extended);
   }

   public BlockFace getFacing() {
      return (BlockFace)this.get(FACING);
   }

   public void setFacing(BlockFace facing) {
      this.set(FACING, facing);
   }

   public Set<BlockFace> getFaces() {
      return this.getValues(FACING);
   }
}
