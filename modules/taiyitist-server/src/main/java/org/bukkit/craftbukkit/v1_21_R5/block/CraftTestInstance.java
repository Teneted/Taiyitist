package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.TestInstance;

public class CraftTestInstance extends CraftBlockEntityState<TestInstanceBlockEntity> implements TestInstance {
   public CraftTestInstance(World world, TestInstanceBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftTestInstance(CraftTestInstance state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftTestInstance copy() {
      return new CraftTestInstance(this, (Location)null);
   }

   public CraftTestInstance copy(Location location) {
      return new CraftTestInstance(this, location);
   }
}
