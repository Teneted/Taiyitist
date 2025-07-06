package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;

public class CraftFurnaceFurnace extends CraftFurnace<FurnaceBlockEntity> {
   public CraftFurnaceFurnace(World world, FurnaceBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftFurnaceFurnace(CraftFurnaceFurnace state, Location location) {
      super((CraftFurnace)state, (Location)location);
   }

   public CraftFurnaceFurnace copy() {
      return new CraftFurnaceFurnace(this, (Location)null);
   }

   public CraftFurnaceFurnace copy(Location location) {
      return new CraftFurnaceFurnace(this, location);
   }
}
