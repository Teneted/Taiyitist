package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftBed extends CraftBlockData implements Bed, Directional {
   private static final CraftBlockStateEnum<?, Bed.Part> PART = getEnum(BedBlock.class, "part", Bed.Part.class);
   private static final BooleanProperty OCCUPIED = getBoolean(BedBlock.class, "occupied");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(BedBlock.class, "facing", BlockFace.class);

   public CraftBed() {
   }

   public CraftBed(BlockState state) {
      super(state);
   }

   public Bed.Part getPart() {
      return (Bed.Part)this.get(PART);
   }

   public void setPart(Bed.Part part) {
      this.set(PART, part);
   }

   public boolean isOccupied() {
      return (Boolean)this.get(OCCUPIED);
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
