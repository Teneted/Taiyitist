package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftDropper extends CraftBlockData implements Dispenser, Directional {
   private static final BooleanProperty TRIGGERED = getBoolean(DropperBlock.class, "triggered");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(DropperBlock.class, "facing", BlockFace.class);

   public CraftDropper() {
   }

   public CraftDropper(BlockState state) {
      super(state);
   }

   public boolean isTriggered() {
      return (Boolean)this.get(TRIGGERED);
   }

   public void setTriggered(boolean triggered) {
      this.set(TRIGGERED, triggered);
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
