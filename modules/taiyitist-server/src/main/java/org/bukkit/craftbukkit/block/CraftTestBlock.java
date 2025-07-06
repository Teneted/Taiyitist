package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.TestBlock;

public class CraftTestBlock extends CraftBlockEntityState<TestBlockEntity> implements TestBlock {
   public CraftTestBlock(World world, TestBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftTestBlock(CraftTestBlock state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftTestBlock copy() {
      return new CraftTestBlock(this, (Location)null);
   }

   public CraftTestBlock copy(Location location) {
      return new CraftTestBlock(this, location);
   }
}
