package org.bukkit.craftbukkit.block.data.type;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.ChiseledBookshelf;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftChiseledBookshelf extends CraftBlockData implements ChiseledBookshelf {
   private static final BooleanProperty[] SLOT_OCCUPIED = new BooleanProperty[]{getBoolean("slot_0_occupied"), getBoolean("slot_1_occupied"), getBoolean("slot_2_occupied"), getBoolean("slot_3_occupied"), getBoolean("slot_4_occupied"), getBoolean("slot_5_occupied")};

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
}
