package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Smoker;

public class CraftSmoker extends CraftFurnace<SmokerBlockEntity> implements Smoker {
   public CraftSmoker(World world, SmokerBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftSmoker(CraftSmoker state, Location location) {
      super((CraftFurnace)state, (Location)location);
   }

   public CraftSmoker copy() {
      return new CraftSmoker(this, (Location)null);
   }

   public CraftSmoker copy(Location location) {
      return new CraftSmoker(this, location);
   }
}
