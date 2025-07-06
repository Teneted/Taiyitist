package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.phys.Vec2;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.inventory.CraftInventoryChiseledBookshelf;
import org.bukkit.inventory.ChiseledBookshelfInventory;
import org.bukkit.util.Vector;

public class CraftChiseledBookshelf extends CraftBlockEntityState<ChiseledBookShelfBlockEntity> implements ChiseledBookshelf {
   public CraftChiseledBookshelf(World world, ChiseledBookShelfBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftChiseledBookshelf(CraftChiseledBookshelf state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public int getLastInteractedSlot() {
      return ((ChiseledBookShelfBlockEntity)this.getSnapshot()).getLastInteractedSlot();
   }

   public void setLastInteractedSlot(int lastInteractedSlot) {
      ((ChiseledBookShelfBlockEntity)this.getSnapshot()).lastInteractedSlot = lastInteractedSlot;
   }

   public ChiseledBookshelfInventory getSnapshotInventory() {
      return new CraftInventoryChiseledBookshelf((ChiseledBookShelfBlockEntity)this.getSnapshot());
   }

   public ChiseledBookshelfInventory getInventory() {
      return (ChiseledBookshelfInventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventoryChiseledBookshelf((ChiseledBookShelfBlockEntity)this.getTileEntity()));
   }

   public int getSlot(Vector clickVector) {
      BlockFace facing = ((Directional)this.getBlockData()).getFacing();
      Vec2 faceVector;
      switch (facing) {
         case NORTH:
            faceVector = new Vec2((float)(1.0 - clickVector.getX()), (float)clickVector.getY());
            break;
         case SOUTH:
            faceVector = new Vec2((float)clickVector.getX(), (float)clickVector.getY());
            break;
         case WEST:
            faceVector = new Vec2((float)clickVector.getZ(), (float)clickVector.getY());
            break;
         case EAST:
            faceVector = new Vec2((float)(1.0 - clickVector.getZ()), (float)clickVector.getY());
            break;
         case DOWN:
         case UP:
         default:
            return -1;
      }

      return getHitSlot(faceVector);
   }

   private static int getHitSlot(Vec2 vec2f) {
      int i = vec2f.y >= 0.5F ? 0 : 1;
      int j = ChiseledBookShelfBlock.getSection(vec2f.x);
      return j + i * 3;
   }

   public CraftChiseledBookshelf copy() {
      return new CraftChiseledBookshelf(this, (Location)null);
   }

   public CraftChiseledBookshelf copy(Location location) {
      return new CraftChiseledBookshelf(this, location);
   }
}
