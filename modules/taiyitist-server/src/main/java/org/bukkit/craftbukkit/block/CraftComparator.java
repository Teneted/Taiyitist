package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Comparator;

public class CraftComparator extends CraftBlockEntityState<ComparatorBlockEntity> implements Comparator {
   public CraftComparator(World world, ComparatorBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftComparator(CraftComparator state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftComparator copy() {
      return new CraftComparator(this, (Location)null);
   }

   public CraftComparator copy(Location location) {
      return new CraftComparator(this, location);
   }
}
