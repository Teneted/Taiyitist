package org.bukkit.craftbukkit.block;

import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftBlockEntityState<T extends BlockEntity> extends CraftBlockState implements TileState {
   private final T tileEntity;
   private final T snapshot;

   public CraftBlockEntityState(World world, T tileEntity) {
      super(world, tileEntity.getBlockPos(), tileEntity.getBlockState());
      this.tileEntity = tileEntity;
      this.snapshot = this.createSnapshot(tileEntity);
      this.load(this.snapshot);
   }

   protected CraftBlockEntityState(CraftBlockEntityState<T> state, Location location) {
      super(state, location);
      this.tileEntity = this.createSnapshot(state.snapshot);
      this.snapshot = this.tileEntity;
      this.loadData(state.getSnapshotNBT());
   }

   public void refreshSnapshot() {
      this.load(this.tileEntity);
   }

   protected RegistryAccess getRegistryAccess() {
      LevelAccessor worldHandle = this.getWorldHandle();
      return worldHandle != null ? worldHandle.registryAccess() : MinecraftServer.getDefaultRegistryAccess();
   }

   private T createSnapshot(T tileEntity) {
      if (tileEntity == null) {
         return null;
      } else {
         CompoundTag nbtTagCompound = tileEntity.saveWithFullMetadata(this.getRegistryAccess());
         T snapshot = BlockEntity.loadStatic(this.getPosition(), this.getHandle(), nbtTagCompound, this.getRegistryAccess());
         return snapshot;
      }
   }

   public Set<DataComponentType<?>> applyComponents(DataComponentMap datacomponentmap, DataComponentPatch datacomponentpatch) {
      Set<DataComponentType<?>> result = this.snapshot.applyComponentsSet(datacomponentmap, datacomponentpatch);
      this.load(this.snapshot);
      return result;
   }

   public DataComponentMap collectComponents() {
      return this.snapshot.collectComponents();
   }

   private ValueInput createInput(CompoundTag nbtTagCompound) {
      return TagValueInput.create(ProblemReporter.DISCARDING, this.getRegistryAccess(), nbtTagCompound);
   }

   private TagValueOutput createOutput() {
      return TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.getRegistryAccess());
   }

   public void loadData(CompoundTag nbtTagCompound) {
      this.loadData(this.createInput(nbtTagCompound));
   }

   public void loadData(ValueInput input) {
      this.snapshot.loadWithComponents(input);
      this.load(this.snapshot);
   }

   private void copyData(T from, T to) {
      CompoundTag nbtTagCompound = from.saveWithFullMetadata(this.getRegistryAccess());
      to.loadWithComponents(this.createInput(nbtTagCompound));
   }

   protected T getTileEntity() {
      return this.tileEntity;
   }

   protected T getSnapshot() {
      return this.snapshot;
   }

   protected BlockEntity getTileEntityFromWorld() {
      this.requirePlaced();
      return this.getWorldHandle().getBlockEntity(this.getPosition());
   }

   public CompoundTag getSnapshotNBT() {
      this.applyTo(this.snapshot);
      return this.snapshot.saveWithFullMetadata(this.getRegistryAccess());
   }

   public ValueInput getSnapshotInput() {
      return this.createInput(this.getSnapshotNBT());
   }

   public TagValueOutput getItemNBT() {
      this.applyTo(this.snapshot);
      TagValueOutput output = this.createOutput();
      this.snapshot.saveCustomOnly(output);
      this.snapshot.removeComponentsFromTag(output);
      return output;
   }

   public void addEntityType(TagValueOutput nbt) {
      BlockEntity.addEntityType(nbt, this.snapshot.getType());
   }

   public CompoundTag getUpdateNBT() {
      this.applyTo(this.snapshot);
      return this.snapshot.getUpdateTag(this.getRegistryAccess());
   }

   protected void load(T tileEntity) {
      if (tileEntity != null && tileEntity != this.snapshot) {
         this.copyData(tileEntity, this.snapshot);
      }

   }

   protected void applyTo(T tileEntity) {
      if (tileEntity != null && tileEntity != this.snapshot) {
         this.copyData(this.snapshot, tileEntity);
      }

   }

   protected boolean isApplicable(BlockEntity tileEntity) {
      return tileEntity != null && this.tileEntity.getClass() == tileEntity.getClass();
   }

   public boolean update(boolean force, boolean applyPhysics) {
      boolean result = super.update(force, applyPhysics);
      if (result && this.isPlaced()) {
         BlockEntity tile = this.getTileEntityFromWorld();
         if (this.isApplicable(tile)) {
            this.applyTo(tile);
            tile.setChanged();
         }
      }

      return result;
   }

   public PersistentDataContainer getPersistentDataContainer() {
      return this.getSnapshot().persistentDataContainer;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket(@NotNull Location location) {
      return new ClientboundBlockEntityDataPacket(CraftLocation.toBlockPosition(location), this.snapshot.getType(), this.getUpdateNBT());
   }

   public CraftBlockEntityState<T> copy() {
      return new CraftBlockEntityState(this, (Location)null);
   }

   public CraftBlockEntityState<T> copy(Location location) {
      return new CraftBlockEntityState(this, location);
   }
}
