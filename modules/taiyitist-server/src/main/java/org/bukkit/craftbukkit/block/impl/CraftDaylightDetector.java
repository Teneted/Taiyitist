package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftDaylightDetector extends CraftBlockData implements DaylightDetector, AnaloguePowerable {
   private static final BooleanProperty INVERTED = getBoolean(DaylightDetectorBlock.class, "inverted");
   private static final IntegerProperty POWER = getInteger(DaylightDetectorBlock.class, "power");

   public CraftDaylightDetector() {
   }

   public CraftDaylightDetector(BlockState state) {
      super(state);
   }

   public boolean isInverted() {
      return (Boolean)this.get(INVERTED);
   }

   public void setInverted(boolean inverted) {
      this.set(INVERTED, inverted);
   }

   public int getPower() {
      return (Integer)this.get(POWER);
   }

   public void setPower(int power) {
      this.set(POWER, power);
   }

   public int getMaximumPower() {
      return getMax(POWER);
   }
}
