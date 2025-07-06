package org.bukkit.craftbukkit.block.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.ChiseledBookshelf;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftChiseledBookShelf extends CraftBlockData implements ChiseledBookshelf, Directional {
   private static final BooleanProperty[] SLOT_OCCUPIED = new BooleanProperty[]{getBoolean(ChiseledBookShelfBlock.class, "slot_0_occupied"), getBoolean(ChiseledBookShelfBlock.class, "slot_1_occupied"), getBoolean(ChiseledBookShelfBlock.class, "slot_2_occupied"), getBoolean(ChiseledBookShelfBlock.class, "slot_3_occupied"), getBoolean(ChiseledBookShelfBlock.class, "slot_4_occupied"), getBoolean(ChiseledBookShelfBlock.class, "slot_5_occupied")};
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(ChiseledBookShelfBlock.class, "facing", BlockFace.class);

   public CraftChiseledBookShelf() {
   }

   public CraftChiseledBookShelf(BlockState state) {
      super(state);
   }

   public boolean isSlotOccupied(int slot) {
      return (Boolean)this.get(SLOT_OCCUPIED[slot]);
   }

   public void setSlotOccupied(int slot, boolean has) {
      this.set(SLOT_OCCUPIED[slot], has);
   }

   public Set<Integer> getOccupiedSlots() {
      ImmutableSet.Builder<Integer> slots = ImmutableSet.builder();

      for(int index = 0; index < this.getMaximumOccupiedSlots(); ++index) {
         if (this.isSlotOccupied(index)) {
            slots.add(index);
         }
      }

      return slots.build();
   }

   public int getMaximumOccupiedSlots() {
      return SLOT_OCCUPIED.length;
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
