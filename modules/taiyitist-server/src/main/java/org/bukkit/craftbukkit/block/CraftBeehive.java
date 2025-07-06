package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Beehive;
import org.bukkit.craftbukkit.entity.CraftBee;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Bee;

public class CraftBeehive extends CraftBlockEntityState<BeehiveBlockEntity> implements Beehive {
   public CraftBeehive(World world, BeehiveBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftBeehive(CraftBeehive state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public Location getFlower() {
      BlockPos flower = ((BeehiveBlockEntity)this.getSnapshot()).savedFlowerPos;
      return flower == null ? null : CraftLocation.toBukkit(flower, this.getWorld());
   }

   public void setFlower(Location location) {
      Preconditions.checkArgument(location == null || this.getWorld().equals(location.getWorld()), "Flower must be in same world");
      ((BeehiveBlockEntity)this.getSnapshot()).savedFlowerPos = location == null ? null : CraftLocation.toBlockPosition(location);
   }

   public boolean isFull() {
      return ((BeehiveBlockEntity)this.getSnapshot()).isFull();
   }

   public boolean isSedated() {
      return this.isPlaced() && ((BeehiveBlockEntity)this.getTileEntity()).isSedated();
   }

   public int getEntityCount() {
      return ((BeehiveBlockEntity)this.getSnapshot()).getOccupantCount();
   }

   public int getMaxEntities() {
      return ((BeehiveBlockEntity)this.getSnapshot()).maxBees;
   }

   public void setMaxEntities(int max) {
      Preconditions.checkArgument(max > 0, "Max bees must be more than 0");
      ((BeehiveBlockEntity)this.getSnapshot()).maxBees = max;
   }

   public List<Bee> releaseEntities() {
      this.ensureNoWorldGeneration();
      List<Bee> bees = new ArrayList();
      if (this.isPlaced()) {
         BeehiveBlockEntity beehive = (BeehiveBlockEntity)this.getTileEntityFromWorld();
         Iterator var3 = beehive.releaseBees(this.getHandle(), BeeReleaseStatus.BEE_RELEASED, true).iterator();

         while(var3.hasNext()) {
            Entity bee = (Entity)var3.next();
            bees.add((Bee)bee.getBukkitEntity());
         }
      }

      return bees;
   }

   public void addEntity(Bee entity) {
      Preconditions.checkArgument(entity != null, "Entity must not be null");
      ((BeehiveBlockEntity)this.getSnapshot()).addOccupant(((CraftBee)entity).getHandle());
   }

   public CraftBeehive copy() {
      return new CraftBeehive(this, (Location)null);
   }

   public CraftBeehive copy(Location location) {
      return new CraftBeehive(this, location);
   }
}
