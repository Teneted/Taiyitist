package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftSpawnCategory;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pose;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRemoveEvent.Cause;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.spigotmc.AsyncCatcher;

public abstract class CraftEntity implements Entity {
   private static PermissibleBase perm;
   private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();
   protected final CraftServer server;
   protected net.minecraft.world.entity.Entity entity;
   private final EntityType entityType;
   private EntityDamageEvent lastDamageEvent;
   private final CraftPersistentDataContainer persistentDataContainer;
   private final Entity.Spigot spigot;

   public CraftEntity(CraftServer server, net.minecraft.world.entity.Entity entity) {
      this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
      this.spigot = new Entity.Spigot() {
         public void sendMessage(BaseComponent component) {
         }

         public void sendMessage(BaseComponent... components) {
         }

         public void sendMessage(UUID sender, BaseComponent... components) {
         }

         public void sendMessage(UUID sender, BaseComponent component) {
         }
      };
      this.server = server;
      this.entity = entity;
      this.entityType = CraftEntityType.minecraftToBukkit(entity.getType());
   }

   public static <T extends net.minecraft.world.entity.Entity> CraftEntity getEntity(CraftServer server, T entity) {
      Preconditions.checkArgument(entity != null, "Unknown entity");
      if (entity instanceof Player && !(entity instanceof ServerPlayer)) {
         return new CraftHumanEntity(server, (Player)entity);
      } else if (entity instanceof EnderDragonPart) {
         EnderDragonPart complexPart = (EnderDragonPart)entity;
         return (CraftEntity)(complexPart.parentMob instanceof EnderDragon ? new CraftEnderDragonPart(server, complexPart) : new CraftComplexPart(server, complexPart));
      } else {
         CraftEntityTypes.EntityTypeData<?, T> entityTypeData = CraftEntityTypes.getEntityTypeData(CraftEntityType.minecraftToBukkit(entity.getType()));
         if (entityTypeData != null) {
            return (CraftEntity)entityTypeData.convertFunction().apply(server, entity);
         } else {
            Class var10002 = entity == null ? null : entity.getClass();
            throw new AssertionError("Unknown entity " + String.valueOf(var10002));
         }
      }
   }

   public Location getLocation() {
      return CraftLocation.toBukkit(this.entity.position(), this.getWorld(), this.entity.getBukkitYaw(), this.entity.getXRot());
   }

   public Location getLocation(Location loc) {
      if (loc != null) {
         loc.setWorld(this.getWorld());
         loc.setX(this.entity.getX());
         loc.setY(this.entity.getY());
         loc.setZ(this.entity.getZ());
         loc.setYaw(this.entity.getBukkitYaw());
         loc.setPitch(this.entity.getXRot());
      }

      return loc;
   }

   public Vector getVelocity() {
      return CraftVector.toBukkit(this.entity.getDeltaMovement());
   }

   public void setVelocity(Vector velocity) {
      Preconditions.checkArgument(velocity != null, "velocity");
      velocity.checkFinite();
      this.entity.setDeltaMovement(CraftVector.toNMS(velocity));
      this.entity.hurtMarked = true;
   }

   public double getHeight() {
      return (double)this.getHandle().getBbHeight();
   }

   public double getWidth() {
      return (double)this.getHandle().getBbWidth();
   }

   public BoundingBox getBoundingBox() {
      AABB bb = this.getHandle().getBoundingBox();
      return new BoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
   }

   public boolean isOnGround() {
      return this.entity instanceof AbstractArrow ? ((AbstractArrow)this.entity).isInGround() : this.entity.onGround();
   }

   public boolean isInWater() {
      return this.entity.isInWater();
   }

   public World getWorld() {
      return this.entity.level().getWorld();
   }

   public void setRotation(float yaw, float pitch) {
      NumberConversions.checkFinite(pitch, "pitch not finite");
      NumberConversions.checkFinite(yaw, "yaw not finite");
      yaw = Location.normalizeYaw(yaw);
      pitch = Location.normalizePitch(pitch);
      this.entity.setYRot(yaw);
      this.entity.setXRot(pitch);
      this.entity.yRotO = yaw;
      this.entity.xRotO = pitch;
      this.entity.setYHeadRot(yaw);
   }

   public boolean teleport(Location location) {
      return this.teleport(location, TeleportCause.PLUGIN);
   }

   public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
      Preconditions.checkArgument(location != null, "location cannot be null");
      location.checkFinite();
      if (!this.entity.isVehicle() && !this.entity.isRemoved()) {
         this.entity.stopRiding();
         if (location.getWorld() != null && !location.getWorld().equals(this.getWorld())) {
            Preconditions.checkState(!this.entity.bridge$generation(), "Cannot teleport entity to an other world during world generation");
            this.entity.teleport(new TeleportTransition(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toVec3D(location), Vec3.ZERO, location.getPitch(), location.getYaw(), Set.of(), TeleportTransition.DO_NOTHING/*, TeleportCause.PLUGIN*/)); // Taiyitist - TODO fixme
            return true;
         } else {
            this.entity.absSnapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            this.entity.setYHeadRot(location.getYaw());
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean teleport(Entity destination) {
      return this.teleport(destination.getLocation());
   }

   public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
      return this.teleport(destination.getLocation(), cause);
   }

   public List<Entity> getNearbyEntities(double x, double y, double z) {
      Preconditions.checkState(!this.entity.bridge$generation(), "Cannot get nearby entities during world generation");
      AsyncCatcher.catchOp("getNearbyEntities");
      List<net.minecraft.world.entity.Entity> notchEntityList = this.entity.level().getEntities(this.entity, this.entity.getBoundingBox().inflate(x, y, z), Predicates.alwaysTrue());
      List<Entity> bukkitEntityList = new ArrayList(notchEntityList.size());
      Iterator var9 = notchEntityList.iterator();

      while(var9.hasNext()) {
         net.minecraft.world.entity.Entity e = (net.minecraft.world.entity.Entity)var9.next();
         bukkitEntityList.add(e.getBukkitEntity());
      }

      return bukkitEntityList;
   }

   public int getEntityId() {
      return this.entity.getId();
   }

   public int getFireTicks() {
      return this.entity.getRemainingFireTicks();
   }

   public int getMaxFireTicks() {
      return this.entity.getFireImmuneTicks();
   }

   public void setFireTicks(int ticks) {
      this.entity.setRemainingFireTicks(ticks);
   }

   public void setVisualFire(boolean fire) {
      this.getHandle().hasVisualFire = fire;
   }

   public boolean isVisualFire() {
      return this.getHandle().hasVisualFire;
   }

   public int getFreezeTicks() {
      return this.getHandle().getTicksFrozen();
   }

   public int getMaxFreezeTicks() {
      return this.getHandle().getTicksRequiredToFreeze();
   }

   public void setFreezeTicks(int ticks) {
      Preconditions.checkArgument(0 <= ticks, "Ticks (%s) cannot be less than 0", ticks);
      this.getHandle().setTicksFrozen(ticks);
   }

   public boolean isFrozen() {
      return this.getHandle().isFullyFrozen();
   }

   public void remove() {
      this.entity.taiyitist$setPluginRemoved(true);
      this.entity.discard(this.getHandle().bridge$generation() ? null : Cause.PLUGIN);
   }

   public boolean isDead() {
      return !this.entity.isAlive();
   }

   public boolean isValid() {
      return this.entity.isAlive() && this.entity.bridge$valid() && this.entity.isChunkLoaded() && this.isInWorld();
   }

   public Server getServer() {
      return this.server;
   }

   public boolean isPersistent() {
      return this.entity.bridge$persist();
   }

   public void setPersistent(boolean persistent) {
      this.entity.taiyitist$setPersist(persistent);
   }

   public Vector getMomentum() {
      return this.getVelocity();
   }

   public void setMomentum(Vector value) {
      this.setVelocity(value);
   }

   public Entity getPassenger() {
      return this.isEmpty() ? null : ((net.minecraft.world.entity.Entity)this.getHandle().passengers.get(0)).getBukkitEntity();
   }

   public boolean setPassenger(Entity passenger) {
      Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");
      if (passenger instanceof CraftEntity) {
         this.eject();
         return ((CraftEntity)passenger).getHandle().startRiding(this.getHandle());
      } else {
         return false;
      }
   }

   public List<Entity> getPassengers() {
      return Lists.newArrayList(Lists.transform(this.getHandle().passengers, (input) -> {
         return input.getBukkitEntity();
      }));
   }

   public boolean addPassenger(Entity passenger) {
      Preconditions.checkArgument(passenger != null, "Entity passenger cannot be null");
      Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");
      return ((CraftEntity)passenger).getHandle().startRiding(this.getHandle(), true);
   }

   public boolean removePassenger(Entity passenger) {
      Preconditions.checkArgument(passenger != null, "Entity passenger cannot be null");
      ((CraftEntity)passenger).getHandle().stopRiding();
      return true;
   }

   public boolean isEmpty() {
      return !this.getHandle().isVehicle();
   }

   public boolean eject() {
      if (this.isEmpty()) {
         return false;
      } else {
         this.getHandle().ejectPassengers();
         return true;
      }
   }

   public float getFallDistance() {
      return (float)this.getHandle().fallDistance;
   }

   public void setFallDistance(float distance) {
      this.getHandle().fallDistance = (double)distance;
   }

   public void setLastDamageCause(EntityDamageEvent event) {
      this.lastDamageEvent = event;
   }

   public EntityDamageEvent getLastDamageCause() {
      return this.lastDamageEvent;
   }

   public UUID getUniqueId() {
      return this.getHandle().getUUID();
   }

   public int getTicksLived() {
      return this.getHandle().tickCount;
   }

   public void setTicksLived(int value) {
      Preconditions.checkArgument(value > 0, "Age value (%s) must be greater than 0", value);
      this.getHandle().tickCount = value;
   }

   public net.minecraft.world.entity.Entity getHandle() {
      return this.entity;
   }

   public final EntityType getType() {
      return this.entityType;
   }

   public void playEffect(EntityEffect type) {
      Preconditions.checkArgument(type != null, "Type cannot be null");
      Preconditions.checkState(!this.entity.bridge$generation(), "Cannot play effect during world generation");
      if (type.getApplicable().isInstance(this)) {
         this.getHandle().level().broadcastEntityEvent(this.getHandle(), type.getData());
      }

   }

   public Sound getSwimSound() {
      return CraftSound.minecraftToBukkit(this.getHandle().getSwimSound0());
   }

   public Sound getSwimSplashSound() {
      return CraftSound.minecraftToBukkit(this.getHandle().getSwimSplashSound0());
   }

   public Sound getSwimHighSpeedSplashSound() {
      return CraftSound.minecraftToBukkit(this.getHandle().getSwimHighSpeedSplashSound0());
   }

   public void setHandle(net.minecraft.world.entity.Entity entity) {
      this.entity = entity;
   }

   public String toString() {
      return "CraftEntity{id=" + this.getEntityId() + "}";
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftEntity other = (CraftEntity)obj;
         return this.getEntityId() == other.getEntityId();
      }
   }

   public int hashCode() {
      int hash = 7;
      hash = 29 * hash + this.getEntityId();
      return hash;
   }

   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
   }

   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getEntityMetadata().getMetadata(this, metadataKey);
   }

   public boolean hasMetadata(String metadataKey) {
      return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
   }

   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
   }

   public boolean isInsideVehicle() {
      return this.getHandle().isPassenger();
   }

   public boolean leaveVehicle() {
      if (!this.isInsideVehicle()) {
         return false;
      } else {
         this.getHandle().stopRiding();
         return true;
      }
   }

   public Entity getVehicle() {
      return !this.isInsideVehicle() ? null : this.getHandle().getVehicle().getBukkitEntity();
   }

   public void setCustomName(String name) {
      if (name != null && name.length() > 256) {
         name = name.substring(0, 256);
      }

      this.getHandle().setCustomName(CraftChatMessage.fromStringOrNull(name));
   }

   public String getCustomName() {
      Component name = this.getHandle().getCustomName();
      return name == null ? null : CraftChatMessage.fromComponent(name);
   }

   public void setCustomNameVisible(boolean flag) {
      this.getHandle().setCustomNameVisible(flag);
   }

   public boolean isCustomNameVisible() {
      return this.getHandle().isCustomNameVisible();
   }

   public void setVisibleByDefault(boolean visible) {
      if (this.getHandle().bridge$visibleByDefault() != visible) {
         Iterator var2;
         org.bukkit.entity.Player player;
         if (visible) {
            var2 = this.server.getOnlinePlayers().iterator();

            while(var2.hasNext()) {
               player = (org.bukkit.entity.Player)var2.next();
               ((CraftPlayer)player).resetAndShowEntity(this);
            }
         } else {
            var2 = this.server.getOnlinePlayers().iterator();

            while(var2.hasNext()) {
               player = (org.bukkit.entity.Player)var2.next();
               ((CraftPlayer)player).resetAndHideEntity(this);
            }
         }

         this.getHandle().taiyitist$setVisibleByDefault(visible);
      }

   }

   public boolean isVisibleByDefault() {
      return this.getHandle().bridge$visibleByDefault();
   }

   public Set<org.bukkit.entity.Player> getTrackedBy() {
      Preconditions.checkState(!this.entity.bridge$generation(), "Cannot get tracking players during world generation");
      ImmutableSet.Builder<org.bukkit.entity.Player> players = ImmutableSet.builder();
      ServerLevel world = ((CraftWorld)this.getWorld()).getHandle();
      ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)world.getChunkSource().chunkMap.entityMap.get(this.getEntityId());
      if (entityTracker != null) {
         Iterator var4 = entityTracker.seenBy.iterator();

         while(var4.hasNext()) {
            ServerPlayerConnection connection = (ServerPlayerConnection)var4.next();
            players.add(connection.getPlayer().getBukkitEntity());
         }
      }

      return players.build();
   }

   public void sendMessage(String message) {
   }

   public void sendMessage(String... messages) {
   }

   public void sendMessage(UUID sender, String message) {
      this.sendMessage(message);
   }

   public void sendMessage(UUID sender, String... messages) {
      this.sendMessage(messages);
   }

   public String getName() {
      return CraftChatMessage.fromComponent(this.getHandle().getName());
   }

   public boolean isPermissionSet(String name) {
      return getPermissibleBase().isPermissionSet(name);
   }

   public boolean isPermissionSet(Permission perm) {
      return getPermissibleBase().isPermissionSet(perm);
   }

   public boolean hasPermission(String name) {
      return getPermissibleBase().hasPermission(name);
   }

   public boolean hasPermission(Permission perm) {
      return getPermissibleBase().hasPermission(perm);
   }

   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
      return getPermissibleBase().addAttachment(plugin, name, value);
   }

   public PermissionAttachment addAttachment(Plugin plugin) {
      return getPermissibleBase().addAttachment(plugin);
   }

   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
      return getPermissibleBase().addAttachment(plugin, name, value, ticks);
   }

   public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
      return getPermissibleBase().addAttachment(plugin, ticks);
   }

   public void removeAttachment(PermissionAttachment attachment) {
      getPermissibleBase().removeAttachment(attachment);
   }

   public void recalculatePermissions() {
      getPermissibleBase().recalculatePermissions();
   }

   public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return getPermissibleBase().getEffectivePermissions();
   }

   public boolean isOp() {
      return getPermissibleBase().isOp();
   }

   public void setOp(boolean value) {
      getPermissibleBase().setOp(value);
   }

   public void setGlowing(boolean flag) {
      this.getHandle().setGlowingTag(flag);
   }

   public boolean isGlowing() {
      return this.getHandle().isCurrentlyGlowing();
   }

   public void setInvulnerable(boolean flag) {
      this.getHandle().setInvulnerable(flag);
   }

   public boolean isInvulnerable() {
      return this.getHandle().isInvulnerableToBase(this.getHandle().damageSources().generic());
   }

   public boolean isSilent() {
      return this.getHandle().isSilent();
   }

   public void setSilent(boolean flag) {
      this.getHandle().setSilent(flag);
   }

   public boolean hasGravity() {
      return !this.getHandle().isNoGravity();
   }

   public void setGravity(boolean gravity) {
      this.getHandle().setNoGravity(!gravity);
   }

   public int getPortalCooldown() {
      return this.getHandle().portalCooldown;
   }

   public void setPortalCooldown(int cooldown) {
      this.getHandle().portalCooldown = cooldown;
   }

   public Set<String> getScoreboardTags() {
      return this.getHandle().getTags();
   }

   public boolean addScoreboardTag(String tag) {
      return this.getHandle().addTag(tag);
   }

   public boolean removeScoreboardTag(String tag) {
      return this.getHandle().removeTag(tag);
   }

   public PistonMoveReaction getPistonMoveReaction() {
      return PistonMoveReaction.getById(this.getHandle().getPistonPushReaction().ordinal());
   }

   public BlockFace getFacing() {
      return CraftBlock.notchToBlockFace(this.getHandle().getMotionDirection());
   }

   public CraftPersistentDataContainer getPersistentDataContainer() {
      return this.persistentDataContainer;
   }

   public Pose getPose() {
      return Pose.values()[this.getHandle().getPose().ordinal()];
   }

   public SpawnCategory getSpawnCategory() {
      return CraftSpawnCategory.toBukkit(this.getHandle().getType().getCategory());
   }

   public boolean isInWorld() {
      return this.getHandle().bridge$inWorld();
   }

   public String getAsString() {
      TagValueOutput tag = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.getHandle().registryAccess());
      return !this.getHandle().saveAsPassenger(tag/*, false Taiyitist TODO fixme*/) ? null : tag.buildResult().toString();
   }

   public EntitySnapshot createSnapshot() {
      return CraftEntitySnapshot.create(this);
   }

   public Entity copy() {
      net.minecraft.world.entity.Entity copy = this.copy(this.getHandle().level());
      Preconditions.checkArgument(copy != null, "Error creating new entity.");
      return copy.getBukkitEntity();
   }

   public Entity copy(Location location) {
      Preconditions.checkArgument(location.getWorld() != null, "Location has no world");
      net.minecraft.world.entity.Entity copy = this.copy((Level)((CraftWorld)location.getWorld()).getHandle());
      Preconditions.checkArgument(copy != null, "Error creating new entity.");
      copy.setPos(location.getX(), location.getY(), location.getZ());
      return location.getWorld().addEntity(copy.getBukkitEntity());
   }

   private net.minecraft.world.entity.Entity copy(Level level) {
      TagValueOutput compoundTag = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.getHandle().registryAccess());
      this.getHandle().saveAsPassenger(compoundTag/*, false*/);// Taiyisit - TODO fixme
      return net.minecraft.world.entity.EntityType.loadEntityRecursive(compoundTag.buildResult(), level, EntitySpawnReason.LOAD, Function.identity());
   }

   public void storeBukkitValues(ValueOutput output) {
      if (!this.persistentDataContainer.isEmpty()) {
         this.persistentDataContainer.store(output.child("BukkitValues"));
      }

   }

   public void readBukkitValues(ValueInput input) {
      input.child("BukkitValues").ifPresent((base) -> {
         this.persistentDataContainer.putAll(base);
      });
   }

   protected CompoundTag save() {
      TagValueOutput nbttagcompound = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.getHandle().registryAccess());
      nbttagcompound.putString("id", this.getHandle().getEncodeId());
      this.getHandle().saveWithoutId(nbttagcompound);
      return nbttagcompound.buildResult();
   }

   protected void update() {
      if (this.getHandle().isAlive()) {
         ServerLevel world = ((CraftWorld)this.getWorld()).getHandle();
         ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)world.getChunkSource().chunkMap.entityMap.get(this.getEntityId());
         if (entityTracker != null) {
            entityTracker.broadcast(this.getHandle().getAddEntityPacket(entityTracker.serverEntity));
         }
      }
   }

   public void update(ServerPlayer player) {
      if (this.getHandle().isAlive()) {
         ServerLevel world = ((CraftWorld)this.getWorld()).getHandle();
         ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)world.getChunkSource().chunkMap.entityMap.get(this.getEntityId());
         if (entityTracker != null) {
            player.connection.send(this.getHandle().getAddEntityPacket(entityTracker.serverEntity));
         }
      }
   }

   private static PermissibleBase getPermissibleBase() {
      if (perm == null) {
         perm = new PermissibleBase(new ServerOperator() {
            public boolean isOp() {
               return false;
            }

            public void setOp(boolean value) {
            }
         });
      }

      return perm;
   }

   public Entity.Spigot spigot() {
      return this.spigot;
   }
}
