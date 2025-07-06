package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Candle;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftCandle extends CraftBlockData implements Candle, Lightable, Waterlogged {
   private static final IntegerProperty CANDLES = getInteger(CandleBlock.class, "candles");
   private static final BooleanProperty LIT = getBoolean(CandleBlock.class, "lit");
   private static final BooleanProperty WATERLOGGED = getBoolean(CandleBlock.class, "waterlogged");

   public CraftCandle() {
   }

   public CraftCandle(BlockState state) {
      super(state);
   }

   public int getCandles() {
      return (Integer)this.get(CANDLES);
   }

   public void setCandles(int candles) {
      this.set(CANDLES, candles);
   }

   public int getMaximumCandles() {
      return getMax(CANDLES);
   }

   public boolean isLit() {
      return (Boolean)this.get(LIT);
   }

   public void setLit(boolean lit) {
      this.set(LIT, lit);
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
