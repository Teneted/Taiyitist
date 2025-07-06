package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftStepAbstract extends CraftBlockData implements Slab, Waterlogged {
   private static final CraftBlockStateEnum<?, Slab.Type> TYPE = getEnum(SlabBlock.class, "type", Slab.Type.class);
   private static final BooleanProperty WATERLOGGED = getBoolean(SlabBlock.class, "waterlogged");

   public CraftStepAbstract() {
   }

   public CraftStepAbstract(BlockState state) {
      super(state);
   }

   public Slab.Type getType() {
      return (Slab.Type)this.get(TYPE);
   }

   public void setType(Slab.Type type) {
      this.set(TYPE, type);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
