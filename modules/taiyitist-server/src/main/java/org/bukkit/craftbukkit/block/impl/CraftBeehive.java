package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftBeehive extends CraftBlockData implements Beehive, Directional {
   private static final IntegerProperty HONEY_LEVEL = getInteger(BeehiveBlock.class, "honey_level");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(BeehiveBlock.class, "facing", BlockFace.class);

   public CraftBeehive() {
   }

   public CraftBeehive(BlockState state) {
      super(state);
   }

   public int getHoneyLevel() {
      return (Integer)this.get(HONEY_LEVEL);
   }

   public void setHoneyLevel(int honeyLevel) {
      this.set(HONEY_LEVEL, honeyLevel);
   }

   public int getMaximumHoneyLevel() {
      return getMax(HONEY_LEVEL);
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
