package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlastFurnace;

public class CraftBlastFurnace extends CraftFurnace<BlastFurnaceBlockEntity> implements BlastFurnace {
   public CraftBlastFurnace(World world, BlastFurnaceBlockEntity tileEntity) {
      super((World)world, (AbstractFurnaceBlockEntity)tileEntity);
   }

   protected CraftBlastFurnace(CraftBlastFurnace state, Location location) {
      super((CraftFurnace)state, (Location)location);
   }

   public CraftBlastFurnace copy() {
      return new CraftBlastFurnace(this, (Location)null);
   }

   public CraftBlastFurnace copy(Location location) {
      return new CraftBlastFurnace(this, location);
   }
}
