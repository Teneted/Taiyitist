package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.level.SpawnData;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.SpawnerMinecart;

final class CraftMinecartMobSpawner extends CraftMinecart implements SpawnerMinecart {
   CraftMinecartMobSpawner(CraftServer server, MinecartSpawner entity) {
      super(server, entity);
   }

   public EntityType getSpawnedType() {
      SpawnData spawnData = this.getHandle().getSpawner().nextSpawnData;
      if (spawnData == null) {
         return null;
      } else {
         Optional<net.minecraft.world.entity.EntityType<?>> type = spawnData.getEntityToSpawn().read("id", net.minecraft.world.entity.EntityType.CODEC);
         return (EntityType)type.map(CraftEntityType::minecraftToBukkit).orElse((EntityType) null);
      }
   }

   public void setSpawnedType(EntityType entityType) {
      if (entityType == null) {
         this.getHandle().getSpawner().spawnPotentials = WeightedList.of();
         this.getHandle().getSpawner().nextSpawnData = new SpawnData();
      } else {
         Preconditions.checkArgument(entityType != EntityType.UNKNOWN, "Can't spawn EntityType %s from mob spawners!", entityType);
         RandomSource rand = this.getHandle().level().getRandom();
         this.getHandle().getSpawner().setEntityId(CraftEntityType.bukkitToMinecraft(entityType), this.getHandle().level(), rand, this.getHandle().blockPosition());
      }
   }

   public EntitySnapshot getSpawnedEntity() {
      SpawnData spawnData = this.getHandle().getSpawner().nextSpawnData;
      return spawnData == null ? null : CraftEntitySnapshot.create(spawnData.getEntityToSpawn());
   }

   public void setSpawnedEntity(EntitySnapshot snapshot) {
      CraftCreatureSpawner.setSpawnedEntity(this.getHandle().getSpawner(), snapshot, (SpawnRule)null, (SpawnerEntry.Equipment)null);
   }

   public void setSpawnedEntity(SpawnerEntry spawnerEntry) {
      Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");
      CraftCreatureSpawner.setSpawnedEntity(this.getHandle().getSpawner(), spawnerEntry.getSnapshot(), spawnerEntry.getSpawnRule(), spawnerEntry.getEquipment());
   }

   public void addPotentialSpawn(EntitySnapshot snapshot, int weight, SpawnRule spawnRule) {
      CraftCreatureSpawner.addPotentialSpawn(this.getHandle().getSpawner(), snapshot, weight, spawnRule, (SpawnerEntry.Equipment)null);
   }

   public void addPotentialSpawn(SpawnerEntry spawnerEntry) {
      Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");
      CraftCreatureSpawner.addPotentialSpawn(this.getHandle().getSpawner(), spawnerEntry.getSnapshot(), spawnerEntry.getSpawnWeight(), spawnerEntry.getSpawnRule(), spawnerEntry.getEquipment());
   }

   public void setPotentialSpawns(Collection<SpawnerEntry> entries) {
      CraftCreatureSpawner.setPotentialSpawns(this.getHandle().getSpawner(), entries);
   }

   public List<SpawnerEntry> getPotentialSpawns() {
      return CraftCreatureSpawner.getPotentialSpawns(this.getHandle().getSpawner());
   }

   public int getDelay() {
      return this.getHandle().getSpawner().spawnDelay;
   }

   public void setDelay(int delay) {
      this.getHandle().getSpawner().spawnDelay = delay;
   }

   public int getMinSpawnDelay() {
      return this.getHandle().getSpawner().minSpawnDelay;
   }

   public void setMinSpawnDelay(int spawnDelay) {
      Preconditions.checkArgument(spawnDelay <= this.getMaxSpawnDelay(), "Minimum Spawn Delay must be less than or equal to Maximum Spawn Delay");
      this.getHandle().getSpawner().minSpawnDelay = spawnDelay;
   }

   public int getMaxSpawnDelay() {
      return this.getHandle().getSpawner().maxSpawnDelay;
   }

   public void setMaxSpawnDelay(int spawnDelay) {
      Preconditions.checkArgument(spawnDelay > 0, "Maximum Spawn Delay must be greater than 0.");
      Preconditions.checkArgument(spawnDelay >= this.getMinSpawnDelay(), "Maximum Spawn Delay must be greater than or equal to Minimum Spawn Delay");
      this.getHandle().getSpawner().maxSpawnDelay = spawnDelay;
   }

   public int getMaxNearbyEntities() {
      return this.getHandle().getSpawner().maxNearbyEntities;
   }

   public void setMaxNearbyEntities(int maxNearbyEntities) {
      this.getHandle().getSpawner().maxNearbyEntities = maxNearbyEntities;
   }

   public int getSpawnCount() {
      return this.getHandle().getSpawner().spawnCount;
   }

   public void setSpawnCount(int count) {
      this.getHandle().getSpawner().spawnCount = count;
   }

   public int getRequiredPlayerRange() {
      return this.getHandle().getSpawner().requiredPlayerRange;
   }

   public void setRequiredPlayerRange(int requiredPlayerRange) {
      this.getHandle().getSpawner().requiredPlayerRange = requiredPlayerRange;
   }

   public int getSpawnRange() {
      return this.getHandle().getSpawner().spawnRange;
   }

   public void setSpawnRange(int spawnRange) {
      this.getHandle().getSpawner().spawnRange = spawnRange;
   }

   public MinecartSpawner getHandle() {
      return (MinecartSpawner)this.entity;
   }

   public String toString() {
      return "CraftMinecartMobSpawner";
   }
}
