package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CalibratedSculkSensor;

public class CraftCalibratedSculkSensor extends CraftSculkSensor<CalibratedSculkSensorBlockEntity> implements CalibratedSculkSensor {
   public CraftCalibratedSculkSensor(World world, CalibratedSculkSensorBlockEntity tileEntity) {
      super((World)world, (SculkSensorBlockEntity)tileEntity);
   }

   protected CraftCalibratedSculkSensor(CraftCalibratedSculkSensor state, Location location) {
      super((CraftSculkSensor)state, (Location)location);
   }

   public CraftCalibratedSculkSensor copy() {
      return new CraftCalibratedSculkSensor(this, (Location)null);
   }

   public CraftCalibratedSculkSensor copy(Location location) {
      return new CraftCalibratedSculkSensor(this, location);
   }
}
