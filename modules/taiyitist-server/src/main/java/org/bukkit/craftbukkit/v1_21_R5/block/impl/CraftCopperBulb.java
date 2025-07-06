package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.CopperBulb;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftCopperBulb extends CraftBlockData implements CopperBulb, Lightable, Powerable {
   private static final BooleanProperty LIT = getBoolean(CopperBulbBlock.class, "lit");
   private static final BooleanProperty POWERED = getBoolean(CopperBulbBlock.class, "powered");

   public CraftCopperBulb() {
   }

   public CraftCopperBulb(BlockState state) {
      super(state);
   }

   public boolean isLit() {
      return (Boolean)this.get(LIT);
   }

   public void setLit(boolean lit) {
      this.set(LIT, lit);
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
