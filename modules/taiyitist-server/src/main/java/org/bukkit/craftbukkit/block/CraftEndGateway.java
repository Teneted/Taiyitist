package org.bukkit.craftbukkit.block;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.EndGateway;
import org.bukkit.craftbukkit.util.CraftLocation;

public class CraftEndGateway extends CraftBlockEntityState<TheEndGatewayBlockEntity> implements EndGateway {
   public CraftEndGateway(World world, TheEndGatewayBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftEndGateway(CraftEndGateway state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public Location getExitLocation() {
      BlockPos pos = ((TheEndGatewayBlockEntity)this.getSnapshot()).exitPortal;
      return pos == null ? null : CraftLocation.toBukkit(pos, this.isPlaced() ? this.getWorld() : null);
   }

   public void setExitLocation(Location location) {
      if (location == null) {
         ((TheEndGatewayBlockEntity)this.getSnapshot()).exitPortal = null;
      } else {
         if (!Objects.equals(location.getWorld(), this.isPlaced() ? this.getWorld() : null)) {
            throw new IllegalArgumentException("Cannot set exit location to different world");
         }

         ((TheEndGatewayBlockEntity)this.getSnapshot()).exitPortal = CraftLocation.toBlockPosition(location);
      }

   }

   public boolean isExactTeleport() {
      return ((TheEndGatewayBlockEntity)this.getSnapshot()).exactTeleport;
   }

   public void setExactTeleport(boolean exact) {
      ((TheEndGatewayBlockEntity)this.getSnapshot()).exactTeleport = exact;
   }

   public long getAge() {
      return ((TheEndGatewayBlockEntity)this.getSnapshot()).age;
   }

   public void setAge(long age) {
      ((TheEndGatewayBlockEntity)this.getSnapshot()).age = age;
   }

   protected void applyTo(TheEndGatewayBlockEntity endGateway) {
      super.applyTo(endGateway);
      if (((TheEndGatewayBlockEntity)this.getSnapshot()).exitPortal == null) {
         endGateway.exitPortal = null;
      }

   }

   public CraftEndGateway copy() {
      return new CraftEndGateway(this, (Location)null);
   }

   public CraftEndGateway copy(Location location) {
      return new CraftEndGateway(this, location);
   }
}
