package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.DaylightDetector;

public class CraftDaylightDetector extends CraftBlockEntityState<DaylightDetectorBlockEntity> implements DaylightDetector {
   public CraftDaylightDetector(World world, DaylightDetectorBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftDaylightDetector(CraftDaylightDetector state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftDaylightDetector copy() {
      return new CraftDaylightDetector(this, (Location)null);
   }

   public CraftDaylightDetector copy(Location location) {
      return new CraftDaylightDetector(this, location);
   }
}
