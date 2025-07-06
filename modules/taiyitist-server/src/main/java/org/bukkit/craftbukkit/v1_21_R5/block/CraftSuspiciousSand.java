package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.SuspiciousSand;

public class CraftSuspiciousSand extends CraftBrushableBlock implements SuspiciousSand {
   public CraftSuspiciousSand(World world, BrushableBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftSuspiciousSand(CraftSuspiciousSand state, Location location) {
      super((CraftBrushableBlock)state, (Location)location);
   }

   public CraftSuspiciousSand copy() {
      return new CraftSuspiciousSand(this, (Location)null);
   }

   public CraftSuspiciousSand copy(Location location) {
      return new CraftSuspiciousSand(this, location);
   }
}
