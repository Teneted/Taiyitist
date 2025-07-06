package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.MossyCarpet;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftMossyCarpet extends CraftBlockData implements MossyCarpet {
   private static final BooleanProperty BOTTOM = getBoolean(MossyCarpetBlock.class, "bottom");
   private static final CraftBlockStateEnum<?, MossyCarpet.Height>[] HEIGHTS = new CraftBlockStateEnum[]{getEnum(MossyCarpetBlock.class, "north", MossyCarpet.Height.class), getEnum(MossyCarpetBlock.class, "east", MossyCarpet.Height.class), getEnum(MossyCarpetBlock.class, "south", MossyCarpet.Height.class), getEnum(MossyCarpetBlock.class, "west", MossyCarpet.Height.class)};

   public CraftMossyCarpet() {
   }

   public CraftMossyCarpet(BlockState state) {
      super(state);
   }

   public boolean isBottom() {
      return (Boolean)this.get(BOTTOM);
   }

   public void setBottom(boolean up) {
      this.set(BOTTOM, up);
   }

   public MossyCarpet.Height getHeight(BlockFace face) {
      return (MossyCarpet.Height)this.get(HEIGHTS[face.ordinal()]);
   }

   public void setHeight(BlockFace face, MossyCarpet.Height height) {
      this.set(HEIGHTS[face.ordinal()], height);
   }
}
