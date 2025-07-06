package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftPistonExtension extends CraftBlockData implements PistonHead, TechnicalPiston, Directional {
   private static final BooleanProperty SHORT = getBoolean(PistonHeadBlock.class, "short");
   private static final CraftBlockStateEnum<?, TechnicalPiston.Type> TYPE = getEnum(PistonHeadBlock.class, "type", TechnicalPiston.Type.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(PistonHeadBlock.class, "facing", BlockFace.class);

   public CraftPistonExtension() {
   }

   public CraftPistonExtension(BlockState state) {
      super(state);
   }

   public boolean isShort() {
      return (Boolean)this.get(SHORT);
   }

   public void setShort(boolean _short) {
      this.set(SHORT, _short);
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
