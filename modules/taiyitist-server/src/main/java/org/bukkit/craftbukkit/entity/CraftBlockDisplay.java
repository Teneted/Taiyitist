package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.Display;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.BlockDisplay;

public class CraftBlockDisplay extends CraftDisplay implements BlockDisplay {
   public CraftBlockDisplay(CraftServer server, Display.BlockDisplay entity) {
      super(server, entity);
   }

   public Display.BlockDisplay getHandle() {
      return (Display.BlockDisplay)super.getHandle();
   }

   public String toString() {
      return "CraftBlockDisplay";
   }

   public BlockData getBlock() {
      return CraftBlockData.fromData(this.getHandle().getBlockState());
   }

   public void setBlock(BlockData block) {
      Preconditions.checkArgument(block != null, "Block cannot be null");
      this.getHandle().setBlockState(((CraftBlockData)block).getState());
   }
}
