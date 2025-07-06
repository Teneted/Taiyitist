package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;

public class CraftCreatureSpawner extends CraftBlockEntityState<SpawnerBlockEntity> implements CreatureSpawner {
   public CraftCreatureSpawner(World world, SpawnerBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftCreatureSpawner(CraftCreatureSpawner state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public EntityType getSpawnedType() {
      SpawnData spawnData = ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().nextSpawnData;
      if (spawnData == null) {
         return null;
      } else {
         Optional<net.minecraft.world.entity.EntityType<?>> type = spawnData.getEntityToSpawn().read("id", net.minecraft.world.entity.EntityType.CODEC);
         return (EntityType)type.map(CraftEntityType::minecraftToBukkit).orElse((Object)null);
      }
   }

   public void setSpawnedType(EntityType entityType) {
      if (entityType == null) {
         ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnPotentials = WeightedList.of();
         ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().nextSpawnData = new SpawnData();
      } else {
         Preconditions.checkArgument(entityType != EntityType.UNKNOWN, "Can't spawn EntityType %s from mob spawners!", entityType);
         RandomSource rand = this.isPlaced() ? this.getWorldHandle().getRandom() : RandomSource.create();
         ((SpawnerBlockEntity)this.getSnapshot()).setEntityId(CraftEntityType.bukkitToMinecraft(entityType), rand);
      }
   }

   public EntitySnapshot getSpawnedEntity() {
      SpawnData spawnData = ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().nextSpawnData;
      return spawnData == null ? null : CraftEntitySnapshot.create(spawnData.getEntityToSpawn());
   }

   public void setSpawnedEntity(EntitySnapshot snapshot) {
      setSpawnedEntity(((SpawnerBlockEntity)this.getSnapshot()).getSpawner(), snapshot, (SpawnRule)null, (SpawnerEntry.Equipment)null);
   }

   public void setSpawnedEntity(SpawnerEntry spawnerEntry) {
      Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");
      setSpawnedEntity(((SpawnerBlockEntity)this.getSnapshot()).getSpawner(), spawnerEntry.getSnapshot(), spawnerEntry.getSpawnRule(), spawnerEntry.getEquipment());
   }

   public static void setSpawnedEntity(BaseSpawner spawner, EntitySnapshot snapshot, SpawnRule spawnRule, SpawnerEntry.Equipment equipment) {
      spawner.spawnPotentials = WeightedList.of();
      if (snapshot == null) {
         spawner.nextSpawnData = new SpawnData();
      } else {
         CompoundTag compoundTag = ((CraftEntitySnapshot)snapshot).getData();
         spawner.nextSpawnData = new SpawnData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnRule)), getEquipment(equipment));
      }
   }

   public void addPotentialSpawn(EntitySnapshot snapshot, int weight, SpawnRule spawnRule) {
      addPotentialSpawn(((SpawnerBlockEntity)this.getSnapshot()).getSpawner(), snapshot, weight, spawnRule, (SpawnerEntry.Equipment)null);
   }

   public static void addPotentialSpawn(BaseSpawner spawner, EntitySnapshot snapshot, int weight, SpawnRule spawnRule, SpawnerEntry.Equipment equipment) {
      Preconditions.checkArgument(snapshot != null, "Snapshot cannot be null");
      CompoundTag compoundTag = ((CraftEntitySnapshot)snapshot).getData();
      WeightedList.Builder<SpawnData> builder = WeightedList.builder();
      spawner.spawnPotentials.unwrap().forEach((entry) -> {
         builder.add((SpawnData)entry.value(), entry.weight());
      });
      builder.add(new SpawnData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnRule)), getEquipment(equipment)), weight);
      spawner.spawnPotentials = builder.build();
   }

   public void addPotentialSpawn(SpawnerEntry spawnerEntry) {
      Preconditions.checkArgument(spawnerEntry != null, "Entry cannot be null");
      this.addPotentialSpawn(spawnerEntry.getSnapshot(), spawnerEntry.getSpawnWeight(), spawnerEntry.getSpawnRule());
   }

   public void setPotentialSpawns(Collection<SpawnerEntry> entries) {
      setPotentialSpawns(((SpawnerBlockEntity)this.getSnapshot()).getSpawner(), entries);
   }

   public static void setPotentialSpawns(BaseSpawner spawner, Collection<SpawnerEntry> entries) {
      Preconditions.checkArgument(entries != null, "Entries cannot be null");
      WeightedList.Builder<SpawnData> builder = WeightedList.builder();
      Iterator var3 = entries.iterator();

      while(var3.hasNext()) {
         SpawnerEntry spawnerEntry = (SpawnerEntry)var3.next();
         CompoundTag compoundTag = ((CraftEntitySnapshot)spawnerEntry.getSnapshot()).getData();
         builder.add(new SpawnData(compoundTag, Optional.ofNullable(toMinecraftRule(spawnerEntry.getSpawnRule())), getEquipment(spawnerEntry.getEquipment())), spawnerEntry.getSpawnWeight());
      }

      spawner.spawnPotentials = builder.build();
   }

   public List<SpawnerEntry> getPotentialSpawns() {
      return getPotentialSpawns(((SpawnerBlockEntity)this.getSnapshot()).getSpawner());
   }

   public static List<SpawnerEntry> getPotentialSpawns(BaseSpawner spawner) {
      List<SpawnerEntry> entries = new ArrayList();
      Iterator var2 = spawner.spawnPotentials.unwrap().iterator();

      while(var2.hasNext()) {
         Weighted<SpawnData> entry = (Weighted)var2.next();
         CraftEntitySnapshot snapshot = CraftEntitySnapshot.create(((SpawnData)entry.value()).getEntityToSpawn());
         if (snapshot != null) {
            SpawnRule rule = (SpawnRule)((SpawnData)entry.value()).customSpawnRules().map(CraftCreatureSpawner::fromMinecraftRule).orElse((Object)null);
            entries.add(new SpawnerEntry(snapshot, entry.weight(), rule, getEquipment(((SpawnData)entry.value()).equipment())));
         }
      }

      return entries;
   }

   public static SpawnData.CustomSpawnRules toMinecraftRule(SpawnRule rule) {
      return rule == null ? null : new SpawnData.CustomSpawnRules(new InclusiveRange(rule.getMinBlockLight(), rule.getMaxBlockLight()), new InclusiveRange(rule.getMinSkyLight(), rule.getMaxSkyLight()));
   }

   public static SpawnRule fromMinecraftRule(SpawnData.CustomSpawnRules rule) {
      InclusiveRange<Integer> blockLight = rule.blockLightLimit();
      InclusiveRange<Integer> skyLight = rule.skyLightLimit();
      return new SpawnRule((Integer)blockLight.maxInclusive(), (Integer)blockLight.maxInclusive(), (Integer)skyLight.minInclusive(), (Integer)skyLight.maxInclusive());
   }

   public String getCreatureTypeName() {
      SpawnData spawnData = ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().nextSpawnData;
      if (spawnData == null) {
         return null;
      } else {
         Optional<net.minecraft.world.entity.EntityType<?>> type = spawnData.getEntityToSpawn().read("id", net.minecraft.world.entity.EntityType.CODEC);
         return (String)type.map(CraftEntityType::minecraftToBukkit).map(CraftEntityType::bukkitToString).orElse((Object)null);
      }
   }

   public void setCreatureTypeByName(String creatureType) {
      EntityType type = CraftEntityType.stringToBukkit(creatureType);
      if (type == null) {
         this.setSpawnedType((EntityType)null);
      } else {
         this.setSpawnedType(type);
      }
   }

   public int getDelay() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnDelay;
   }

   public void setDelay(int delay) {
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnDelay = delay;
   }

   public int getMinSpawnDelay() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().minSpawnDelay;
   }

   public void setMinSpawnDelay(int spawnDelay) {
      Preconditions.checkArgument(spawnDelay <= this.getMaxSpawnDelay(), "Minimum Spawn Delay must be less than or equal to Maximum Spawn Delay");
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().minSpawnDelay = spawnDelay;
   }

   public int getMaxSpawnDelay() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().maxSpawnDelay;
   }

   public void setMaxSpawnDelay(int spawnDelay) {
      Preconditions.checkArgument(spawnDelay > 0, "Maximum Spawn Delay must be greater than 0.");
      Preconditions.checkArgument(spawnDelay >= this.getMinSpawnDelay(), "Maximum Spawn Delay must be greater than or equal to Minimum Spawn Delay");
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().maxSpawnDelay = spawnDelay;
   }

   public int getMaxNearbyEntities() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().maxNearbyEntities;
   }

   public void setMaxNearbyEntities(int maxNearbyEntities) {
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().maxNearbyEntities = maxNearbyEntities;
   }

   public int getSpawnCount() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnCount;
   }

   public void setSpawnCount(int count) {
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnCount = count;
   }

   public int getRequiredPlayerRange() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().requiredPlayerRange;
   }

   public void setRequiredPlayerRange(int requiredPlayerRange) {
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().requiredPlayerRange = requiredPlayerRange;
   }

   public int getSpawnRange() {
      return ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnRange;
   }

   public void setSpawnRange(int spawnRange) {
      ((SpawnerBlockEntity)this.getSnapshot()).getSpawner().spawnRange = spawnRange;
   }

   public CraftCreatureSpawner copy() {
      return new CraftCreatureSpawner(this, (Location)null);
   }

   public CraftCreatureSpawner copy(Location location) {
      return new CraftCreatureSpawner(this, location);
   }

   public static Optional<EquipmentTable> getEquipment(SpawnerEntry.Equipment bukkit) {
      return bukkit == null ? Optional.empty() : Optional.of(new EquipmentTable(CraftLootTable.bukkitToMinecraft(bukkit.getEquipmentLootTable()), (Map)bukkit.getDropChances().entrySet().stream().collect(Collectors.toMap((entry) -> {
         return CraftEquipmentSlot.getNMS((EquipmentSlot)entry.getKey());
      }, Map.Entry::getValue))));
   }

   public static SpawnerEntry.Equipment getEquipment(Optional<EquipmentTable> optional) {
      return (SpawnerEntry.Equipment)optional.map((nms) -> {
         return new SpawnerEntry.Equipment(CraftLootTable.minecraftToBukkit(nms.lootTable()), new HashMap((Map)nms.slotDropChances().entrySet().stream().collect(Collectors.toMap((entry) -> {
            return CraftEquipmentSlot.getSlot((net.minecraft.world.entity.EquipmentSlot)entry.getKey());
         }, Map.Entry::getValue))));
      }).orElse((Object)null);
   }
}
