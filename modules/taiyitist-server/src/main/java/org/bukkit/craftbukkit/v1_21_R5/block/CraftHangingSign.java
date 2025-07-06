package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.HangingSign;

public class CraftHangingSign extends CraftSign<HangingSignBlockEntity> implements HangingSign {
   public CraftHangingSign(World world, HangingSignBlockEntity tileEntity) {
      super((World)world, (SignBlockEntity)tileEntity);
   }

   protected CraftHangingSign(CraftHangingSign state, Location location) {
      super((CraftSign)state, (Location)location);
   }

   public CraftHangingSign copy() {
      return new CraftHangingSign(this, (Location)null);
   }

   public CraftHangingSign copy(Location location) {
      return new CraftHangingSign(this, location);
   }
}
