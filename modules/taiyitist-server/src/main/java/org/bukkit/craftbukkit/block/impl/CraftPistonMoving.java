package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftPistonMoving extends CraftBlockData implements TechnicalPiston, Directional {
   private static final CraftBlockStateEnum<?, TechnicalPiston.Type> TYPE = getEnum(MovingPistonBlock.class, "type", TechnicalPiston.Type.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(MovingPistonBlock.class, "facing", BlockFace.class);

   public CraftPistonMoving() {
   }

   public CraftPistonMoving(BlockState state) {
      super(state);
   }

   public TechnicalPiston.Type getType() {
      return (TechnicalPiston.Type)this.get(TYPE);
   }

   public void setType(TechnicalPiston.Type type) {
      this.set(TYPE, type);
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
