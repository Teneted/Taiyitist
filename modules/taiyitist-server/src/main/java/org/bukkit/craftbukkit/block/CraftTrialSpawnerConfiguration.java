package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class CraftTrialSpawnerConfiguration implements TrialSpawnerConfiguration {
   private final TrialSpawnerBlockEntity snapshot;
   private final RegistryAccess registry;
   private int spawnRange;
   private float totalMobs;
   private float simultaneousMobs;
   private float totalMobsAddedPerPlayer;
   private float simultaneousMobsAddedPerPlayer;
   private int ticksBetweenSpawn;
   private WeightedList<SpawnData> spawnPotentialsDefinition;
   private WeightedList<ResourceKey<LootTable>> lootTablesToEject;
   private ResourceKey<LootTable> itemsToDropWhenOminous;

   public CraftTrialSpawnerConfiguration(TrialSpawnerBlockEntity snapshot, RegistryAccess registry) {
      this.snapshot = snapshot;
      this.registry = registry;
   }

   void loadFromConfig(TrialSpawnerConfig minecraft) {
      this.spawnRange = minecraft.spawnRange();
      this.totalMobs = minecraft.totalMobs();
      this.simultaneousMobs = minecraft.simultaneousMobs();
      this.totalMobsAddedPerPlayer = minecraft.totalMobsAddedPerPlayer();
      this.simultaneousMobsAddedPerPlayer = minecraft.simultaneousMobsAddedPerPlayer();
      this.ticksBetweenSpawn = minecraft.ticksBetweenSpawn();
      this.spawnPotentialsDefinition = minecraft.spawnPotentialsDefinition();
      this.lootTablesToEject = minecraft.lootTablesToEject();
      this.itemsToDropWhenOminous = minecraft.itemsToDropWhenOminous();
   }

   public EntityType getSpawnedType() {
      if (this.spawnPotentialsDefinition.isEmpty()) {
         return null;
      } else {
         Optional<net.minecraft.world.entity.EntityType<?>> type = ((SpawnData)((Weighted)this.spawnPotentialsDefinition.unwrap().get(0)).value()).getEntityToSpawn().read("id", net.minecraft.world.entity.EntityType.CODEC);
         return (EntityType)type.map(CraftEntityType::minecraftToBukkit).orElse((EntityType) null);
      }
   }

   public void setSpawnedType(EntityType entityType) {
      if (entityType == null) {
         this.getTrialData().nextSpawnData = Optional.empty();
         this.spawnPotentialsDefinition = WeightedList.of();
      } else {
         Preconditions.checkArgument(entityType != EntityType.UNKNOWN, "Can't spawn EntityType %s from mob spawners!", entityType);
         SpawnData data = new SpawnData();
         data.getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(CraftEntityType.bukkitToMinecraft(entityType)).toString());
         this.getTrialData().nextSpawnData = Optional.of(data);
         this.spawnPotentialsDefinition = WeightedList.of(data);
      }
   }

   public float getBaseSpawnsBeforeCooldown() {
      return this.totalMobs;
   }

   public void setBaseSpawnsBeforeCooldown(float amount) {
      this.totalMobs = amount;
   }

   public float getBaseSimultaneousEntities() {
      return this.simultaneousMobs;
   }

   public void setBaseSimultaneousEntities(float amount) {
      this.simultaneousMobs = amount;
   }

   public float getAdditionalSpawnsBeforeCooldown() {
      return this.totalMobsAddedPerPlayer;
   }

   public void setAdditionalSpawnsBeforeCooldown(float amount) {
      this.totalMobsAddedPerPlayer = amount;
   }

   public float getAdditionalSimultaneousEntities() {
      return this.simultaneousMobsAddedPerPlayer;
   }

   public void setAdditionalSimultaneousEntities(float amount) {
      this.simultaneousMobsAddedPerPlayer = amount;
   }

   public int getDelay() {
      return this.ticksBetweenSpawn;
   }

   public void setDelay(int delay) {
      Preconditions.checkArgument(delay >= 0, "Delay cannot be less than 0");
      this.ticksBetweenSpawn = delay;
   }

   public int getSpawnRange() {
      return this.spawnRange;
   }

   public void setSpawnRange(int spawnRange) {
      this.spawnRange = spawnRange;
   }

   public EntitySnapshot getSpawnedEntity() {
      WeightedList<SpawnData> potentials = this.spawnPotentialsDefinition;
      return potentials.isEmpty() ? null : CraftEntitySnapshot.create(((SpawnData)((Weighted)potentials.unwrap().get(0)).value()).getEntityToSpawn());
   }

   public void setSpawnedEntity(EntitySnapshot snapshot) {
      this.setSpawnedEntity(snapshot, (SpawnRule)null, (SpawnerEntry.Equipment)null);
   }

   public void setSpawnedEntity(SpawnerEntry spawnerEntry) {
      Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");
      this.setSpawnedEntity(spawnerEntry.getSnapshot(), spawnerEntry.getSpawnRule(), spawnerEntry.getEquipment());
   }

   private void setSpawnedEntity(EntitySnapshot snapshot, SpawnRule spawnRule, SpawnerEntry.Equipment equipment) {
      if (snapshot == null) {
         this.getTrialData().nextSpawnData = Optional.empty();
         this.spawnPotentialsDefinition = WeightedList.of();
      } else {
         CompoundTag compoundTag = ((CraftEntitySnapshot)snapshot).getData();
         SpawnData data = new SpawnData(compoundTag, Optional.ofNullable(CraftCreatureSpawner.toMinecraftRule(spawnRule)), CraftCreatureSpawner.getEquipment(equipment));
         this.getTrialData().nextSpawnData = Optional.of(data);
         this.spawnPotentialsDefinition = WeightedList.of(data);
      }
   }

   public void addPotentialSpawn(EntitySnapshot snapshot, int weight, SpawnRule spawnRule) {
      this.addPotentialSpawn(snapshot, weight, spawnRule, (SpawnerEntry.Equipment)null);
   }

   private void addPotentialSpawn(EntitySnapshot snapshot, int weight, SpawnRule spawnRule, SpawnerEntry.Equipment equipment) {
      Preconditions.checkArgument(snapshot != null, "Snapshot cannot be null");
      CompoundTag compoundTag = ((CraftEntitySnapshot)snapshot).getData();
      WeightedList.Builder<SpawnData> builder = WeightedList.builder();
      this.spawnPotentialsDefinition.unwrap().forEach((entry) -> {
         builder.add((SpawnData)entry.value(), entry.weight());
      });
      builder.add(new SpawnData(compoundTag, Optional.ofNullable(CraftCreatureSpawner.toMinecraftRule(spawnRule)), CraftCreatureSpawner.getEquipment(equipment)), weight);
      this.spawnPotentialsDefinition = builder.build();
   }

   public void addPotentialSpawn(SpawnerEntry spawnerEntry) {
      Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");
      this.addPotentialSpawn(spawnerEntry.getSnapshot(), spawnerEntry.getSpawnWeight(), spawnerEntry.getSpawnRule(), spawnerEntry.getEquipment());
   }

   public void setPotentialSpawns(Collection<SpawnerEntry> entries) {
      Preconditions.checkArgument(entries != null, "Entries cannot be null");
      WeightedList.Builder<SpawnData> builder = WeightedList.builder();
      Iterator var3 = entries.iterator();

      while(var3.hasNext()) {
         SpawnerEntry spawnerEntry = (SpawnerEntry)var3.next();
         CompoundTag compoundTag = ((CraftEntitySnapshot)spawnerEntry.getSnapshot()).getData();
         builder.add(new SpawnData(compoundTag, Optional.ofNullable(CraftCreatureSpawner.toMinecraftRule(spawnerEntry.getSpawnRule())), CraftCreatureSpawner.getEquipment(spawnerEntry.getEquipment())), spawnerEntry.getSpawnWeight());
      }

      this.spawnPotentialsDefinition = builder.build();
   }

   public List<SpawnerEntry> getPotentialSpawns() {
      List<SpawnerEntry> entries = new ArrayList();
      Iterator var2 = this.spawnPotentialsDefinition.unwrap().iterator();

      while(var2.hasNext()) {
         Weighted<SpawnData> entry = (Weighted)var2.next();
         CraftEntitySnapshot snapshot = CraftEntitySnapshot.create(((SpawnData)entry.value()).getEntityToSpawn());
         if (snapshot != null) {
            SpawnRule rule = (SpawnRule)((SpawnData)entry.value()).customSpawnRules().map(CraftCreatureSpawner::fromMinecraftRule).orElse((SpawnRule) null);
            entries.add(new SpawnerEntry(snapshot, entry.weight(), rule));
         }
      }

      return entries;
   }

   public Map<org.bukkit.loot.LootTable, Integer> getPossibleRewards() {
      Map<org.bukkit.loot.LootTable, Integer> tables = new HashMap();
      Iterator var2 = this.lootTablesToEject.unwrap().iterator();

      while(var2.hasNext()) {
         Weighted<ResourceKey<LootTable>> entry = (Weighted)var2.next();
         org.bukkit.loot.LootTable table = CraftLootTable.minecraftToBukkit((ResourceKey)entry.value());
         if (table != null) {
            tables.put(table, entry.weight());
         }
      }

      return tables;
   }

   public void addPossibleReward(org.bukkit.loot.LootTable table, int weight) {
      Preconditions.checkArgument(table != null, "Table cannot be null");
      Preconditions.checkArgument(weight >= 1, "Weight must be at least 1");
      WeightedList.Builder<ResourceKey<LootTable>> builder = WeightedList.builder();
      this.lootTablesToEject.unwrap().forEach((entry) -> {
         builder.add((ResourceKey)entry.value(), entry.weight());
      });
      builder.add(CraftLootTable.bukkitToMinecraft(table), weight);
      this.lootTablesToEject = builder.build();
   }

   public void removePossibleReward(org.bukkit.loot.LootTable table) {
      Preconditions.checkArgument(table != null, "Key cannot be null");
      ResourceKey<LootTable> minecraftKey = CraftLootTable.bukkitToMinecraft(table);
      WeightedList.Builder<ResourceKey<LootTable>> builder = WeightedList.builder();
      Iterator var4 = this.lootTablesToEject.unwrap().iterator();

      while(var4.hasNext()) {
         Weighted<ResourceKey<LootTable>> entry = (Weighted)var4.next();
         if (!((ResourceKey)entry.value()).equals(minecraftKey)) {
            builder.add((ResourceKey)entry.value(), entry.weight());
         }
      }

      this.lootTablesToEject = builder.build();
   }

   public void setPossibleRewards(Map<org.bukkit.loot.LootTable, Integer> rewards) {
      if (rewards != null && !rewards.isEmpty()) {
         WeightedList.Builder<ResourceKey<LootTable>> builder = WeightedList.builder();
         rewards.forEach((table, weight) -> {
            Preconditions.checkArgument(table != null, "Table cannot be null");
            Preconditions.checkArgument(weight >= 1, "Weight must be at least 1");
            builder.add(CraftLootTable.bukkitToMinecraft(table), weight);
         });
         this.lootTablesToEject = builder.build();
      } else {
         this.lootTablesToEject = WeightedList.of();
      }
   }

   public int getRequiredPlayerRange() {
      return this.snapshot.trialSpawner.getRequiredPlayerRange();
   }

   public void setRequiredPlayerRange(int requiredPlayerRange) {
      TrialSpawner.FullConfig oldConfig = this.snapshot.trialSpawner.config;
      this.snapshot.trialSpawner.config = new TrialSpawner.FullConfig(oldConfig.normal(), oldConfig.ominous(), oldConfig.targetCooldownLength(), requiredPlayerRange);
   }

   private TrialSpawnerStateData getTrialData() {
      return this.snapshot.getTrialSpawner().getStateData();
   }

   protected TrialSpawnerConfig toMinecraft() {
      return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, this.spawnPotentialsDefinition, this.lootTablesToEject, this.itemsToDropWhenOminous);
   }
}
