package org.bukkit.craftbukkit.block;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Jukebox;
import org.bukkit.craftbukkit.inventory.CraftInventoryJukebox;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.JukeboxInventory;

public class CraftJukebox extends CraftBlockEntityState<JukeboxBlockEntity> implements Jukebox {
   public CraftJukebox(World world, JukeboxBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftJukebox(CraftJukebox state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public JukeboxInventory getSnapshotInventory() {
      return new CraftInventoryJukebox((Container)this.getSnapshot());
   }

   public JukeboxInventory getInventory() {
      return (JukeboxInventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventoryJukebox((Container)this.getTileEntity()));
   }

   public boolean update(boolean force, boolean applyPhysics) {
      boolean result = super.update(force, applyPhysics);
      if (result && this.isPlaced() && this.getType() == Material.JUKEBOX) {
         this.getWorldHandle().setBlock(this.getPosition(), this.data, 3);
         BlockEntity tileEntity = this.getTileEntityFromWorld();
         if (tileEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukebox = (JukeboxBlockEntity)tileEntity;
            jukebox.setTheItem(jukebox.getTheItem());
         }
      }

      return result;
   }

   public Material getPlaying() {
      return this.getRecord().getType();
   }

   public void setPlaying(Material record) {
      if (record == null || CraftItemType.bukkitToMinecraft(record) == null) {
         record = Material.AIR;
      }

      this.setRecord(new ItemStack(record));
   }

   public boolean hasRecord() {
      return (Boolean)this.getHandle().getValue(JukeboxBlock.HAS_RECORD) && !this.getPlaying().isAir();
   }

   public ItemStack getRecord() {
      net.minecraft.world.item.ItemStack record = ((JukeboxBlockEntity)this.getSnapshot()).getTheItem();
      return CraftItemStack.asBukkitCopy(record);
   }

   public void setRecord(ItemStack record) {
      net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(record);
      JukeboxBlockEntity snapshot = (JukeboxBlockEntity)this.getSnapshot();
      snapshot.setSongItemWithoutPlaying(nms, snapshot.getSongPlayer().getTicksSinceSongStarted());
      this.data = (BlockState)this.data.setValue(JukeboxBlock.HAS_RECORD, !nms.isEmpty());
   }

   public boolean isPlaying() {
      this.requirePlaced();
      BlockEntity tileEntity = this.getTileEntityFromWorld();
      boolean var10000;
      if (tileEntity instanceof JukeboxBlockEntity jukebox) {
         if (jukebox.getSongPlayer().isPlaying()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean startPlaying() {
      this.requirePlaced();
      BlockEntity tileEntity = this.getTileEntityFromWorld();
      if (tileEntity instanceof JukeboxBlockEntity jukebox) {
         net.minecraft.world.item.ItemStack var3 = jukebox.getTheItem();
         if (!var3.isEmpty() && !this.isPlaying()) {
            jukebox.tryForcePlaySong();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void stopPlaying() {
      this.requirePlaced();
      BlockEntity tileEntity = this.getTileEntityFromWorld();
      if (tileEntity instanceof JukeboxBlockEntity jukebox) {
         jukebox.getSongPlayer().stop(tileEntity.getLevel(), tileEntity.getBlockState());
      }
   }

   public boolean eject() {
      this.ensureNoWorldGeneration();
      BlockEntity tileEntity = this.getTileEntityFromWorld();
      if (!(tileEntity instanceof JukeboxBlockEntity jukebox)) {
         return false;
      } else {
         boolean result = !jukebox.getTheItem().isEmpty();
         jukebox.popOutTheItem();
         return result;
      }
   }

   public CraftJukebox copy() {
      return new CraftJukebox(this, (Location)null);
   }

   public CraftJukebox copy(Location location) {
      return new CraftJukebox(this, location);
   }
}
