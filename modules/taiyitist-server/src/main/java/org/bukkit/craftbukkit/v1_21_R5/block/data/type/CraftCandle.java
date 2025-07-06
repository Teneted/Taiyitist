package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Candle;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftCandle extends CraftBlockData implements Candle {
   private static final IntegerProperty CANDLES = getInteger("candles");

   public int getCandles() {
      return (Integer)this.get(CANDLES);
   }

   public void setCandles(int candles) {
      this.set(CANDLES, candles);
   }

   public int getMaximumCandles() {
      return getMax(CANDLES);
   }
}
