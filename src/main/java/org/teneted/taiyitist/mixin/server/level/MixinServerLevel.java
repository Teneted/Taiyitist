package org.teneted.taiyitist.mixin.server.level;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTicks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.WorldUUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.taiyitist.TaiyitistMod;
import org.teneted.taiyitist.asm.annotation.CreateConstructor;
import org.teneted.taiyitist.asm.annotation.ShadowConstructor;
import org.teneted.taiyitist.bukkit.BukkitMethodHooks;
import org.teneted.taiyitist.injection.server.level.InjectionServerLevel;
import org.teneted.taiyitist.injection.world.level.storage.InjectionLevelStorageAccess;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level implements WorldGenLevel, InjectionServerLevel {

    private final AtomicReference<CreatureSpawnEvent.SpawnReason> taiyitist$reason = new AtomicReference<>();
    private final AtomicReference<Boolean> taiyitist$timeSkipCancelled = new AtomicReference<>(false);
    @Shadow
    @Final
    public ServerLevelData serverLevelData;
    @Shadow
    @Final
    public PersistentEntitySectionManager<Entity> entityManager;
    public LevelStorageSource.LevelStorageAccess convertable;
    public UUID uuid;
    public PrimaryLevelData K;
    public ResourceKey<LevelStem> typeKey;
    // Banner start
    public AtomicBoolean canaddFreshEntity = new AtomicBoolean(false);
    @Shadow
    @Final
    private ServerChunkCache chunkSource;
    private transient boolean taiyitist$force;
    private transient LightningStrikeEvent.Cause taiyitist$cause;

    protected MixinServerLevel(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }


    @Shadow
    public abstract LevelTicks<Block> getBlockTicks();

    @Shadow
    public abstract List<ServerPlayer> players();
    @Shadow
    @NotNull
    public abstract MinecraftServer getServer();

    @Shadow
    public abstract <T extends ParticleOptions> int sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed);

    @Shadow
    protected abstract void wakeUpAllPlayers();

    @Shadow
    public abstract boolean addFreshEntity(Entity entity);

    @Shadow
    public abstract void addDuringTeleport(Entity entity);

    @Shadow
    public abstract boolean addWithUUID(Entity entity);

    @Shadow
    public abstract ServerChunkCache getChunkSource();

    @Shadow
    protected abstract boolean addEntity(Entity entity);


    @Shadow
    public abstract SavedDataStorage getDataStorage();

    @Shadow
    public abstract boolean isPvpAllowed();

    @ShadowConstructor
    public void taiyitist$constructor(final MinecraftServer server, final Executor executor, final LevelStorageSource.LevelStorageAccess levelStorage, final ServerLevelData levelData, final ResourceKey<Level> dimension, final LevelStem levelStem, final boolean isDebug, final long biomeZoomSeed, final List<CustomSpawner> customSpawners, final boolean tickTime) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(final MinecraftServer server, final Executor executor, final LevelStorageSource.LevelStorageAccess levelStorage, final ServerLevelData levelData, final ResourceKey<Level> dimension, final LevelStem levelStem, final boolean isDebug, final long biomeZoomSeed, final List<CustomSpawner> customSpawners, final boolean tickTime, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen, org.bukkit.generator.BiomeProvider biomeProvider) {
        taiyitist$constructor(server, executor, levelStorage, levelData, dimension, levelStem, isDebug, biomeZoomSeed, customSpawners, tickTime);
        this.taiyitist$setGenerator(gen);
        this.taiyitist$setEnvironment(env);
        this.taiyitist$setBiomeProvider(biomeProvider);
        taiyitist$initWorld(levelStem);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void taiyitist$initWorldServer(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess levelStorage, ServerLevelData levelData, ResourceKey dimension, LevelStem levelStem, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, CallbackInfo ci) {
        this.taiyitist$setPvpMode(this.isPvpAllowed());
        this.convertable = levelStorage;
        var typeKey = ((InjectionLevelStorageAccess) levelStorage).bridge$getTypeKey();
        if (typeKey != null) {
            this.typeKey = typeKey;
        } else {
            var dimensions = BukkitMethodHooks.getServer().registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
            var key = dimensions.getResourceKey(levelStem);
            if (key.isPresent()) {
                this.typeKey = key.get();
            } else {
                TaiyitistMod.LOGGER.warn("Assign {} to unknown level stem {}", dimension.registry(), levelStem);
                this.typeKey = ResourceKey.create(Registries.LEVEL_STEM, dimension.registry());
            }
        }
        if (serverLevelData instanceof PrimaryLevelData) {
            this.K = (PrimaryLevelData) serverLevelData;
        } else if (serverLevelData instanceof DerivedLevelData) {
            ((DerivedLevelData) serverLevelData).setDimType(this.getTypeKey());
        }
        this.uuid = WorldUUID.getUUID(levelStorage.getDimensionPath(this.dimension()).toFile());
        this.getWorldBorder().taiyitist$setWorld((ServerLevel) (Object) this);
        if (this.K != null) {
            this.K.setWorld((ServerLevel) (Object) this);
        }
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;getViewDistance()I"))
    private int taiyitist$setViewDistance(PlayerList instance) {
        return this.bridge$spigotConfig().viewDistance;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;getSimulationDistance()I"))
    private int taiyitist$setSimulationDistance(PlayerList instance) {
        return this.bridge$spigotConfig().simulationDistance;
    }

    @Inject(method = "gameEvent", cancellable = true, at = @At("HEAD"))
    private void taiyitist$gameEventEvent(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context, CallbackInfo ci) {
        var entity = context.sourceEntity();
        var i = holder.value().notificationRadius();
        GenericGameEvent event = new GenericGameEvent(org.bukkit.GameEvent.getByKey(CraftNamespacedKey.fromMinecraft(BuiltInRegistries.GAME_EVENT.getKey(holder.value()))), new Location(this.getWorld(), vec3.x(), vec3.y(), vec3.z()), (entity == null) ? null : entity.getBukkitEntity(), i, !Bukkit.isPrimaryThread());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Override
    public LevelChunk getChunkIfLoaded(int x, int z) {
        return this.chunkSource.getChunk(x, z, false);
    }

    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void taiyitist$tickPortal(Entity entityIn, CallbackInfo ci) {
        entityIn.postTick();
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;rideTick()V"))
    private void taiyitist$tickPortalPassenger(Entity ridingEntity, Entity passengerEntity, CallbackInfo ci) {
        passengerEntity.postTick();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void taiyitist$entityActivationConfigure(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        org.spigotmc.ActivationRange.activateEntities(this); // Spigot
    }

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    private void taiyitist$checkIfActive(Entity entity, CallbackInfo ci) {
        // Spigot start
        if (!org.spigotmc.ActivationRange.checkIfActive(entity)) {
            entity.tickCount++;
            entity.inactiveTick();
            ci.cancel();
            return;
        }
        // Spigot end
    }

    @Override
    public boolean strikeLightning(Entity entity) {
        return this.strikeLightning(entity, LightningStrikeEvent.Cause.UNKNOWN);
    }

    @Override
    public boolean strikeLightning(Entity entity, LightningStrikeEvent.Cause cause) {
        if (taiyitist$cause != null) {
            cause = taiyitist$cause;
            taiyitist$cause = null;
        }
        if (entity.getBukkitEntity() instanceof org.bukkit.entity.LightningStrike) {
            LightningStrikeEvent lightning = CraftEventFactory.callLightningStrikeEvent((LightningStrike) entity.getBukkitEntity(), cause);
            if (lightning.isCancelled()) {
                return false;
            }
        }
        return this.addFreshEntity(entity);
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void taiyitist$snowForm0(BlockPos blockPos, CallbackInfo ci, @Local(name = "topPos") BlockPos blockPos2) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos2, 3);
        craftBlockState.setData(Blocks.ICE.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void taiyitist$snowForm1(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockState blockState2) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos, 3);
        craftBlockState.setData(blockState2);

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void taiyitist$snowForm2(BlockPos blockPos, CallbackInfo ci) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos, 3);
        craftBlockState.setData(Blocks.SNOW.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "save", at = @At(value = "JUMP", ordinal = 0, opcode = Opcodes.IFNULL))
    private void taiyitist$worldSaveEvent(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        Bukkit.getPluginManager().callEvent(new WorldSaveEvent(getWorld()));

    }

    @Inject(method = "unload", at = @At("HEAD"))
    public void taiyitist$closeOnChunkUnloading(LevelChunk chunkIn, CallbackInfo ci) {
        for (BlockEntity tileentity : chunkIn.getBlockEntities().values()) {
            if (tileentity instanceof Container) {
                for (HumanEntity h : Lists.newArrayList(((Container) tileentity).getViewers())) {
                    if (h instanceof CraftHumanEntity) {
                        ((CraftHumanEntity) h).getHandle().closeContainer();
                    }
                }
            }
        }
    }

    @Override
    public <T extends ParticleOptions> int sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed, boolean force) {
        taiyitist$force = force;
        return this.sendParticles(type, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
    }

    @Inject(method = "addEntity", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z"))
    private void taiyitist$addEntityEvent(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        CreatureSpawnEvent.SpawnReason reason = taiyitist$reason.get() == null ? CreatureSpawnEvent.SpawnReason.DEFAULT : taiyitist$reason.get();
        taiyitist$reason.set(null);
        if (!CraftEventFactory.doEntityAddEventCalling((ServerLevel) (Object) this, entityIn, reason)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean canAddFreshEntity() {
        return canaddFreshEntity.getAndSet(false);
    }
    // Banner end

    // Banner - fix mixin
    @Redirect(method = "addFreshEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$fixAddFreshEntity(ServerLevel instance, Entity entity) {
        boolean add = addEntity(entity);
        canaddFreshEntity.set(add);
        return add;
    }


    @Override
    public boolean addFreshEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        pushAddEntityReason(reason);
        return addFreshEntity(entity);
    }

    @Inject(method = "addEntity", at = @At("RETURN"))
    public void taiyitist$resetReason(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$reason.set(null);
    }

    @Override
    public boolean addWithUUID(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        pushAddEntityReason(reason);
        return this.addWithUUID(entity);
    }

    @Override
    public void addDuringTeleport(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        pushAddEntityReason(reason);
        addDuringTeleport(entity);
    }

    @Override
    public boolean tryAddFreshEntityWithPassengers(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        if (entity.getSelfAndPassengers().map(Entity::getUUID).anyMatch(this.entityManager::isLoaded)) {
            return false;
        } else {
            pushAddEntityReason(reason);
            return this.addAllEntities(entity, reason);
        }
    }

    @Override
    public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return addFreshEntity(entity, reason);
    }

    @Inject(method = "getMapData", at = @At("RETURN"))
    private void taiyitist$getMapSetId(MapId mapId, CallbackInfoReturnable<MapItemSavedData> cir) {
        var data = cir.getReturnValue();
        if (data != null) {
            data.taiyitist$setId(mapId);
        }
    }

    @Inject(method = "setMapData", at = @At("HEAD"))
    private void taiyitist$setMapSetId(MapId mapId, MapItemSavedData mapItemSavedData, CallbackInfo ci) {
        mapItemSavedData.taiyitist$setId(mapId);
        MapInitializeEvent event = new MapInitializeEvent(mapItemSavedData.bridge$mapView());
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    @Inject(method = "setMapData", at = @At("HEAD"))
    private void taiyitist$mapSetId(MapId mapId, MapItemSavedData mapItemSavedData, CallbackInfo ci) {
        mapItemSavedData.taiyitist$setId(mapId);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V"))
    private void taiyitist$notWakeIfCancelled(ServerLevel world) {
        if (!taiyitist$timeSkipCancelled.get()) {
            this.wakeUpAllPlayers();
        }
        taiyitist$timeSkipCancelled.set(false);
    }

    @Override
    public boolean addEntitySerialized(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return addWithUUID(entity, reason);
    }

    @Override
    public PrimaryLevelData bridge$serverLevelDataCB() {
        return K;
    }

    @Override
    public LevelStorageSource.LevelStorageAccess bridge$convertable() {
        return convertable;
    }

    @Override
    public ResourceKey<LevelStem> getTypeKey() {
        return typeKey;
    }

    @Override
    public UUID bridge$uuid() {
        return uuid;
    }
}
