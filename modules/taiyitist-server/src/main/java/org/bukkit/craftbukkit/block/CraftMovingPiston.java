package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;

public class CraftMovingPiston extends CraftBlockEntityState<PistonMovingBlockEntity> {
   public CraftMovingPiston(World world, PistonMovingBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftMovingPiston(CraftMovingPiston state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftMovingPiston copy() {
      return new CraftMovingPiston(this, (Location)null);
   }

   public CraftMovingPiston copy(Location location) {
      return new CraftMovingPiston(this, location);
   }
}
