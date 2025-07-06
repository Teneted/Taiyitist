package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;

public class CraftEndPortal extends CraftBlockEntityState<TheEndPortalBlockEntity> {
   public CraftEndPortal(World world, TheEndPortalBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftEndPortal(CraftEndPortal state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftEndPortal copy() {
      return new CraftEndPortal(this, (Location)null);
   }

   public CraftEndPortal copy(Location location) {
      return new CraftEndPortal(this, location);
   }
}
