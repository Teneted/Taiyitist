package org.bukkit.craftbukkit.v1_21_R5.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class CraftTrialSpawner extends CraftBlockEntityState<TrialSpawnerBlockEntity> implements TrialSpawner {
   private CraftTrialSpawnerConfiguration normalConfig;
   private CraftTrialSpawnerConfiguration ominousConfig;

   public CraftTrialSpawner(World world, TrialSpawnerBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftTrialSpawner(CraftTrialSpawner state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public int getCooldownLength() {
      return ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.getTargetCooldownLength();
   }

   public void setCooldownLength(int ticks) {
      net.minecraft.world.level.block.entity.trialspawner.TrialSpawner.FullConfig oldConfig = ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.config;
      ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.config = new net.minecraft.world.level.block.entity.trialspawner.TrialSpawner.FullConfig(oldConfig.normal(), oldConfig.ominous(), ticks, oldConfig.requiredPlayerRange());
   }

   public int getRequiredPlayerRange() {
      return ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.getRequiredPlayerRange();
   }

   public void setRequiredPlayerRange(int requiredPlayerRange) {
      net.minecraft.world.level.block.entity.trialspawner.TrialSpawner.FullConfig oldConfig = ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.config;
      ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.config = new net.minecraft.world.level.block.entity.trialspawner.TrialSpawner.FullConfig(oldConfig.normal(), oldConfig.ominous(), oldConfig.targetCooldownLength(), requiredPlayerRange);
   }

   public Collection<Player> getTrackedPlayers() {
      ImmutableSet.Builder<Player> players = ImmutableSet.builder();
      Iterator var2 = this.getTrialData().detectedPlayers.iterator();

      while(var2.hasNext()) {
         UUID uuid = (UUID)var2.next();
         Player player = Bukkit.getPlayer(uuid);
         if (player != null) {
            players.add(player);
         }
      }

      return players.build();
   }

   public boolean isTrackingPlayer(Player player) {
      Preconditions.checkArgument(player != null, "Player cannot be null");
      return this.getTrialData().detectedPlayers.contains(player.getUniqueId());
   }

   public void startTrackingPlayer(Player player) {
      Preconditions.checkArgument(player != null, "Player cannot be null");
      this.getTrialData().detectedPlayers.add(player.getUniqueId());
   }

   public void stopTrackingPlayer(Player player) {
      Preconditions.checkArgument(player != null, "Player cannot be null");
      this.getTrialData().detectedPlayers.remove(player.getUniqueId());
   }

   public Collection<Entity> getTrackedEntities() {
      ImmutableSet.Builder<Entity> entities = ImmutableSet.builder();
      Iterator var2 = this.getTrialData().currentMobs.iterator();

      while(var2.hasNext()) {
         UUID uuid = (UUID)var2.next();
         Entity entity = Bukkit.getEntity(uuid);
         if (entity != null) {
            entities.add(entity);
         }
      }

      return entities.build();
   }

   public boolean isTrackingEntity(Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      return this.getTrialData().currentMobs.contains(entity.getUniqueId());
   }

   public void startTrackingEntity(Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      this.getTrialData().currentMobs.add(entity.getUniqueId());
   }

   public void stopTrackingEntity(Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      this.getTrialData().currentMobs.remove(entity.getUniqueId());
   }

   public boolean isOminous() {
      return (Boolean)this.getHandle().getValue(TrialSpawnerBlock.OMINOUS);
   }

   public void setOminous(boolean ominous) {
      ((TrialSpawnerBlockEntity)this.getSnapshot()).trialSpawner.isOminous = ominous;
      if (ominous) {
         this.setData((BlockState)this.getHandle().setValue(TrialSpawnerBlock.OMINOUS, true));
      } else {
         this.setData((BlockState)this.getHandle().setValue(TrialSpawnerBlock.OMINOUS, false));
      }
   }

   public TrialSpawnerConfiguration getNormalConfiguration() {
      return this.normalConfig;
   }

   public TrialSpawnerConfiguration getOminousConfiguration() {
      return this.ominousConfig;
   }

   protected void load(TrialSpawnerBlockEntity tileEntity) {
      super.load(tileEntity);
      if (this.normalConfig == null) {
         this.normalConfig = new CraftTrialSpawnerConfiguration((TrialSpawnerBlockEntity)this.getSnapshot(), this.getRegistryAccess());
      }

      if (this.ominousConfig == null) {
         this.ominousConfig = new CraftTrialSpawnerConfiguration((TrialSpawnerBlockEntity)this.getSnapshot(), this.getRegistryAccess());
      }

      if (tileEntity != null) {
         this.normalConfig.loadFromConfig(tileEntity.getTrialSpawner().normalConfig());
         this.ominousConfig.loadFromConfig(tileEntity.getTrialSpawner().ominousConfig());
      }

   }

   protected void applyTo(TrialSpawnerBlockEntity tileEntity) {
      super.applyTo(tileEntity);
      tileEntity.trialSpawner.config = new net.minecraft.world.level.block.entity.trialspawner.TrialSpawner.FullConfig(Holder.direct(this.normalConfig.toMinecraft()), Holder.direct(this.ominousConfig.toMinecraft()), this.getCooldownLength(), this.getRequiredPlayerRange());
   }

   private TrialSpawnerStateData getTrialData() {
      return ((TrialSpawnerBlockEntity)this.getSnapshot()).getTrialSpawner().getStateData();
   }

   public CraftTrialSpawner copy() {
      return new CraftTrialSpawner(this, (Location)null);
   }

   public CraftTrialSpawner copy(Location location) {
      return new CraftTrialSpawner(this, location);
   }
}
