package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Jigsaw;

public class CraftJigsaw extends CraftBlockEntityState<JigsawBlockEntity> implements Jigsaw {
   public CraftJigsaw(World world, JigsawBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftJigsaw(CraftJigsaw state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftJigsaw copy() {
      return new CraftJigsaw(this, (Location)null);
   }

   public CraftJigsaw copy(Location location) {
      return new CraftJigsaw(this, location);
   }
}
