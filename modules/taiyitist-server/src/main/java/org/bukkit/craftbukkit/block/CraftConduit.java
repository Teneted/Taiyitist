package org.bukkit.craftbukkit.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;

public class CraftConduit extends CraftBlockEntityState<ConduitBlockEntity> implements Conduit {
   public CraftConduit(World world, ConduitBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftConduit(CraftConduit state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftConduit copy() {
      return new CraftConduit(this, (Location)null);
   }

   public CraftConduit copy(Location location) {
      return new CraftConduit(this, location);
   }

   public boolean isActive() {
      this.ensureNoWorldGeneration();
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      return conduit != null && conduit.isActive();
   }

   public boolean isHunting() {
      this.ensureNoWorldGeneration();
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      return conduit != null && conduit.isHunting();
   }

   public Collection<Block> getFrameBlocks() {
      this.ensureNoWorldGeneration();
      Collection<Block> blocks = new ArrayList();
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      if (conduit != null) {
         Iterator var3 = conduit.effectBlocks.iterator();

         while(var3.hasNext()) {
            BlockPos position = (BlockPos)var3.next();
            blocks.add(CraftBlock.at(this.getWorldHandle(), position));
         }
      }

      return blocks;
   }

   public int getFrameBlockCount() {
      this.ensureNoWorldGeneration();
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      return conduit != null ? conduit.effectBlocks.size() : 0;
   }

   public int getRange() {
      this.ensureNoWorldGeneration();
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      return conduit != null ? BukkitMethodHooks.getRange(conduit.effectBlocks) : 0;
   }

   public boolean setTarget(LivingEntity target) {
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      if (conduit == null) {
         return false;
      } else {
         EntityReference<net.minecraft.world.entity.LivingEntity> currentTarget = conduit.destroyTarget;
         if (target == null) {
            if (currentTarget == null) {
               return false;
            }

            conduit.destroyTarget = null;
         } else {
            net.minecraft.world.entity.LivingEntity newTarget = ((CraftLivingEntity)target).getHandle();
            if (currentTarget != null && currentTarget.matches(newTarget)) {
               return false;
            }

            conduit.destroyTarget = new EntityReference(newTarget);
         }

         ConduitBlockEntity.updateAndAttackTarget((ServerLevel) conduit.getLevel(), this.getPosition(), this.data, conduit, conduit.effectBlocks.size() >= 42/*, false*/);// Taiyitist - TODO fixme
         return true;
      }
   }

   public LivingEntity getTarget() {
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      if (conduit == null) {
         return null;
      } else {
         net.minecraft.world.entity.LivingEntity nmsEntity = (net.minecraft.world.entity.LivingEntity)EntityReference.get(conduit.destroyTarget, conduit.getLevel(), net.minecraft.world.entity.LivingEntity.class);
         return nmsEntity != null ? (LivingEntity)nmsEntity.getBukkitEntity() : null;
      }
   }

   public boolean hasTarget() {
      ConduitBlockEntity conduit = (ConduitBlockEntity)this.getTileEntityFromWorld();
      net.minecraft.world.entity.LivingEntity destroyTarget = conduit != null ? (net.minecraft.world.entity.LivingEntity)EntityReference.get(conduit.destroyTarget, conduit.getLevel(), net.minecraft.world.entity.LivingEntity.class) : null;
      return destroyTarget != null && destroyTarget.isAlive();
   }

   public BoundingBox getHuntingArea() {
      AABB bounds = ConduitBlockEntity.getDestroyRangeAABB(this.getPosition());
      return new BoundingBox(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
   }
}
