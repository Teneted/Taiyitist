package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftBed extends CraftBlockData implements Bed {
   private static final CraftBlockStateEnum<?, Bed.Part> PART = getEnum("part", Bed.Part.class);
   private static final BooleanProperty OCCUPIED = getBoolean("occupied");

   public Bed.Part getPart() {
      return (Bed.Part)this.get(PART);
   }

   public void setPart(Bed.Part part) {
      this.set(PART, part);
   }

   public boolean isOccupied() {
      return (Boolean)this.get(OCCUPIED);
   }
}
