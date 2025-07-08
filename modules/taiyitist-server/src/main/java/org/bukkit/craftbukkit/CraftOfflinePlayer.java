package org.bukkit.craftbukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.BanList.Type;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.entity.memory.CraftMemoryMapper;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;

@SerializableAs("Player")
public class CraftOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {
   private final GameProfile profile;
   private final CraftServer server;
   private final PlayerDataStorage storage;

   protected CraftOfflinePlayer(CraftServer server, GameProfile profile) {
      this.server = server;
      this.profile = profile;
      this.storage = server.console.playerDataStorage;
   }

   public boolean isOnline() {
      return this.getPlayer() != null;
   }

   public String getName() {
      Player player = this.getPlayer();
      if (player != null) {
         return player.getName();
      } else if (!this.profile.getName().isEmpty()) {
         return this.profile.getName();
      } else {
         ValueInput data = this.getBukkitData();
         return data != null ? data.getStringOr("lastKnownName", (String)null) : null;
      }
   }

   public UUID getUniqueId() {
      return this.profile.getId();
   }

   public PlayerProfile getPlayerProfile() {
      return new CraftPlayerProfile(this.profile);
   }

   public Server getServer() {
      return this.server;
   }

   public boolean isOp() {
      return this.server.getHandle().isOp(this.profile);
   }

   public void setOp(boolean value) {
      if (value != this.isOp()) {
         if (value) {
            this.server.getHandle().op(this.profile);
         } else {
            this.server.getHandle().deop(this.profile);
         }

      }
   }

   public boolean isBanned() {
      return ((ProfileBanList)this.server.getBanList(Type.PROFILE)).isBanned(this.getPlayerProfile());
   }

   public BanEntry<PlayerProfile> ban(String reason, Date expires, String source) {
      return ((ProfileBanList)this.server.getBanList(Type.PROFILE)).addBan(this.getPlayerProfile(), reason, expires, source);
   }

   public BanEntry<PlayerProfile> ban(String reason, Instant expires, String source) {
      return ((ProfileBanList)this.server.getBanList(Type.PROFILE)).addBan(this.getPlayerProfile(), reason, expires, source);
   }

   public BanEntry<PlayerProfile> ban(String reason, Duration duration, String source) {
      return ((ProfileBanList)this.server.getBanList(Type.PROFILE)).addBan(this.getPlayerProfile(), reason, duration, source);
   }

   public void setBanned(boolean value) {
      if (value) {
         ((ProfileBanList)this.server.getBanList(Type.PROFILE)).addBan(this.getPlayerProfile(), (String)null, (Date)null, (String)null);
      } else {
         ((ProfileBanList)this.server.getBanList(Type.PROFILE)).pardon(this.getPlayerProfile());
      }

   }

   public boolean isWhitelisted() {
      return this.server.getHandle().getWhiteList().isWhiteListed(this.profile);
   }

   public void setWhitelisted(boolean value) {
      if (value) {
         this.server.getHandle().getWhiteList().add(new UserWhiteListEntry(this.profile));
      } else {
         this.server.getHandle().getWhiteList().remove(this.profile);
      }

   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("UUID", this.profile.getId().toString());
      return result;
   }

   public static OfflinePlayer deserialize(Map<String, Object> args) {
      return args.get("name") != null ? Bukkit.getServer().getOfflinePlayer((String)args.get("name")) : Bukkit.getServer().getOfflinePlayer(UUID.fromString((String)args.get("UUID")));
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "[UUID=" + String.valueOf(this.profile.getId()) + "]";
   }

   public Player getPlayer() {
      return this.server.getPlayer(this.getUniqueId());
   }

   public boolean equals(Object obj) {
      if (obj instanceof OfflinePlayer other) {
         return this.getUniqueId() != null && other.getUniqueId() != null ? this.getUniqueId().equals(other.getUniqueId()) : false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hash = 5;
      hash = 97 * hash + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
      return hash;
   }

   private ValueInput getData() {
      return (ValueInput)this.storage.load(this.profile.getName(), this.profile.getId().toString(), ProblemReporter.DISCARDING, this.server.getServer().registryAccess()).orElse((Object)null);
   }

   private ValueInput getBukkitData() {
      ValueInput result = this.getData();
      if (result != null) {
         result = result.childOrEmpty("bukkit");
      }

      return result;
   }

   private File getDataFile() {
      return new File(this.storage.getPlayerDir(), String.valueOf(this.getUniqueId()) + ".dat");
   }

   public long getFirstPlayed() {
      Player player = this.getPlayer();
      if (player != null) {
         return player.getFirstPlayed();
      } else {
         ValueInput data = this.getBukkitData();
         if (data != null) {
            Optional<Long> firstPlayed = data.getLong("firstPlayed");
            if (firstPlayed.isPresent()) {
               return (Long)firstPlayed.get();
            } else {
               File file = this.getDataFile();
               return file.lastModified();
            }
         } else {
            return 0L;
         }
      }
   }

   public long getLastPlayed() {
      Player player = this.getPlayer();
      if (player != null) {
         return player.getLastPlayed();
      } else {
         ValueInput data = this.getBukkitData();
         if (data != null) {
            Optional<Long> lastPlayed = data.getLong("lastPlayed");
            if (lastPlayed.isPresent()) {
               return (Long)lastPlayed.get();
            } else {
               File file = this.getDataFile();
               return file.lastModified();
            }
         } else {
            return 0L;
         }
      }
   }

   public boolean hasPlayedBefore() {
      return this.getData() != null;
   }

   public Location getLastDeathLocation() {
      return (Location)this.getData().read("LastDeathLocation", GlobalPos.CODEC).map(CraftMemoryMapper::fromNms).orElse((Location) null);
   }

   public Location getLocation() {
      ValueInput data = this.getData();
      if (data == null) {
         return null;
      } else {
         Vec3 position = (Vec3)data.read("Pos", Vec3.CODEC).orElse((Vec3) null);
         Vec2 rotation = (Vec2)data.read("Rotation", Vec2.CODEC).orElse((Vec2) null);
         if (position != null && rotation != null) {
            UUID uuid = new UUID(data.getLongOr("WorldUUIDMost", 0L), data.getLongOr("WorldUUIDLeast", 0L));
            return CraftLocation.toBukkit(position, this.server.getWorld(uuid), rotation.x, rotation.y);
         } else {
            return null;
         }
      }
   }

   public Location getBedSpawnLocation() {
      return this.getRespawnLocation();
   }

   public Location getRespawnLocation() {
      ValueInput data = this.getData();
      if (data == null) {
         return null;
      } else {
         ServerPlayer.RespawnConfig respawn = (ServerPlayer.RespawnConfig)data.read("respawn", RespawnConfig.CODEC).orElse((RespawnConfig) null);
         if (respawn != null) {
            ServerLevel world = this.server.getServer().getLevel(respawn.dimension());
            if (world == null) {
               world = this.server.getServer().overworld();
            }

            return CraftLocation.toBukkit((BlockPos)respawn.pos(), world.getWorld(), respawn.angle(), 0.0F);
         } else if (data.read("SpawnX", Codec.INT).isPresent() && data.read("SpawnY", Codec.INT).isPresent() && data.read("SpawnZ", Codec.INT).isPresent()) {
            String spawnWorld = data.getStringOr("SpawnWorld", "");
            if (spawnWorld.equals("")) {
               spawnWorld = ((World)this.server.getWorlds().get(0)).getName();
            }

            return new Location(this.server.getWorld(spawnWorld), (double)data.getIntOr("SpawnX", 0), (double)data.getIntOr("SpawnY", 0), (double)data.getIntOr("SpawnZ", 0));
         } else {
            return null;
         }
      }
   }

   public void setMetadata(String metadataKey, MetadataValue metadataValue) {
      this.server.getPlayerMetadata().setMetadata(this, metadataKey, metadataValue);
   }

   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
   }

   public boolean hasMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
   }

   public void removeMetadata(String metadataKey, Plugin plugin) {
      this.server.getPlayerMetadata().removeMetadata(this, metadataKey, plugin);
   }

   private ServerStatsCounter getStatisticManager() {
      return this.server.getHandle().getPlayerStats(this.getUniqueId(), this.getName());
   }

   public void incrementStatistic(Statistic statistic) {
      if (this.isOnline()) {
         this.getPlayer().incrementStatistic(statistic);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.incrementStatistic(manager, statistic, (ServerPlayer)null);
         manager.save();
      }

   }

   public void decrementStatistic(Statistic statistic) {
      if (this.isOnline()) {
         this.getPlayer().decrementStatistic(statistic);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.decrementStatistic(manager, statistic, (ServerPlayer)null);
         manager.save();
      }

   }

   public int getStatistic(Statistic statistic) {
      return this.isOnline() ? this.getPlayer().getStatistic(statistic) : CraftStatistic.getStatistic(this.getStatisticManager(), statistic);
   }

   public void incrementStatistic(Statistic statistic, int amount) {
      if (this.isOnline()) {
         this.getPlayer().incrementStatistic(statistic, amount);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.incrementStatistic(manager, statistic, amount, (ServerPlayer)null);
         manager.save();
      }

   }

   public void decrementStatistic(Statistic statistic, int amount) {
      if (this.isOnline()) {
         this.getPlayer().decrementStatistic(statistic, amount);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.decrementStatistic(manager, statistic, amount, (ServerPlayer)null);
         manager.save();
      }

   }

   public void setStatistic(Statistic statistic, int newValue) {
      if (this.isOnline()) {
         this.getPlayer().setStatistic(statistic, newValue);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.setStatistic(manager, statistic, newValue, (ServerPlayer)null);
         manager.save();
      }

   }

   public void incrementStatistic(Statistic statistic, Material material) {
      if (this.isOnline()) {
         this.getPlayer().incrementStatistic(statistic, material);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.incrementStatistic(manager, statistic, (Material)material, (ServerPlayer)null);
         manager.save();
      }

   }

   public void decrementStatistic(Statistic statistic, Material material) {
      if (this.isOnline()) {
         this.getPlayer().decrementStatistic(statistic, material);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.decrementStatistic(manager, statistic, (Material)material, (ServerPlayer)null);
         manager.save();
      }

   }

   public int getStatistic(Statistic statistic, Material material) {
      return this.isOnline() ? this.getPlayer().getStatistic(statistic, material) : CraftStatistic.getStatistic(this.getStatisticManager(), statistic, material);
   }

   public void incrementStatistic(Statistic statistic, Material material, int amount) {
      if (this.isOnline()) {
         this.getPlayer().incrementStatistic(statistic, material, amount);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.incrementStatistic(manager, statistic, (Material)material, amount, (ServerPlayer)null);
         manager.save();
      }

   }

   public void decrementStatistic(Statistic statistic, Material material, int amount) {
      if (this.isOnline()) {
         this.getPlayer().decrementStatistic(statistic, material, amount);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.decrementStatistic(manager, statistic, (Material)material, amount, (ServerPlayer)null);
         manager.save();
      }

   }

   public void setStatistic(Statistic statistic, Material material, int newValue) {
      if (this.isOnline()) {
         this.getPlayer().setStatistic(statistic, material, newValue);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.setStatistic(manager, statistic, (Material)material, newValue, (ServerPlayer)null);
         manager.save();
      }

   }

   public void incrementStatistic(Statistic statistic, EntityType entityType) {
      if (this.isOnline()) {
         this.getPlayer().incrementStatistic(statistic, entityType);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.incrementStatistic(manager, statistic, (EntityType)entityType, (ServerPlayer)null);
         manager.save();
      }

   }

   public void decrementStatistic(Statistic statistic, EntityType entityType) {
      if (this.isOnline()) {
         this.getPlayer().decrementStatistic(statistic, entityType);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.decrementStatistic(manager, statistic, (EntityType)entityType, (ServerPlayer)null);
         manager.save();
      }

   }

   public int getStatistic(Statistic statistic, EntityType entityType) {
      return this.isOnline() ? this.getPlayer().getStatistic(statistic, entityType) : CraftStatistic.getStatistic(this.getStatisticManager(), statistic, entityType);
   }

   public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) {
      if (this.isOnline()) {
         this.getPlayer().incrementStatistic(statistic, entityType, amount);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.incrementStatistic(manager, statistic, (EntityType)entityType, amount, (ServerPlayer)null);
         manager.save();
      }

   }

   public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
      if (this.isOnline()) {
         this.getPlayer().decrementStatistic(statistic, entityType, amount);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.decrementStatistic(manager, statistic, (EntityType)entityType, amount, (ServerPlayer)null);
         manager.save();
      }

   }

   public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
      if (this.isOnline()) {
         this.getPlayer().setStatistic(statistic, entityType, newValue);
      } else {
         ServerStatsCounter manager = this.getStatisticManager();
         CraftStatistic.setStatistic(manager, statistic, (EntityType)entityType, newValue, (ServerPlayer)null);
         manager.save();
      }

   }
}
