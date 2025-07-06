package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreakingHeart;

public class CraftCreakingHeart extends CraftBlockEntityState<CreakingHeartBlockEntity> implements CreakingHeart {
   public CraftCreakingHeart(World world, CreakingHeartBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftCreakingHeart(CraftCreakingHeart state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftCreakingHeart copy() {
      return new CraftCreakingHeart(this, (Location)null);
   }

   public CraftCreakingHeart copy(Location location) {
      return new CraftCreakingHeart(this, location);
   }
}
