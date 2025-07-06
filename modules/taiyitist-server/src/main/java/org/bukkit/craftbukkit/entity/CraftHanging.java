package org.bukkit.craftbukkit.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.HangingEntity;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Hanging;

public class CraftHanging extends CraftBlockAttachedEntity implements Hanging {
   public CraftHanging(CraftServer server, HangingEntity entity) {
      super(server, entity);
   }

   public BlockFace getAttachedFace() {
      return this.getFacing().getOppositeFace();
   }

   public void setFacingDirection(BlockFace face) {
      this.setFacingDirection(face, false);
   }

   public boolean setFacingDirection(BlockFace face, boolean force) {
      HangingEntity hanging = this.getHandle();
      Direction dir = hanging.getDirection();
      switch (face) {
         case SOUTH -> this.getHandle().setDirection(Direction.SOUTH);
         case WEST -> this.getHandle().setDirection(Direction.WEST);
         case NORTH -> this.getHandle().setDirection(Direction.NORTH);
         case EAST -> this.getHandle().setDirection(Direction.EAST);
         default -> throw new IllegalArgumentException(String.format("%s is not a valid facing direction", face));
      }

      if (!force && !this.getHandle().bridge$generation() && !hanging.survives()) {
         hanging.setDirection(dir);
         return false;
      } else {
         return true;
      }
   }

   public BlockFace getFacing() {
      Direction direction = this.getHandle().getDirection();
      return direction == null ? BlockFace.SELF : CraftBlock.notchToBlockFace(direction);
   }

   public HangingEntity getHandle() {
      return (HangingEntity)this.entity;
   }

   public String toString() {
      return "CraftHanging";
   }
}
