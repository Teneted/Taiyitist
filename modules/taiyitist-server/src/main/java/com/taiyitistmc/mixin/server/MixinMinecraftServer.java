package com.taiyitistmc.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.taiyitistmc.asm.annotation.TransformAccess;
import com.taiyitistmc.bukkit.BukkitFieldHooks;
import com.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import com.taiyitistmc.config.TaiyitistConfig;
import com.taiyitistmc.config.TaiyitistConfigUtil;
import com.taiyitistmc.injection.server.InjectionMinecraftServer;
import com.mojang.datafixers.DataFixer;
import io.izzel.arclight.mixin.Decorate;
import io.izzel.arclight.mixin.DecorationOps;
import io.izzel.arclight.mixin.Local;

import java.lang.management.ManagementFactory;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spigotmc.WatchdogThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements InjectionMinecraftServer {

    private static final int TPS = 20;
    @Shadow @Final public static org.slf4j.Logger LOGGER;
    @Mutable
    @Shadow
    @Final
    private static long OVERLOADED_THRESHOLD_NANOS;
    // @formatter:on
    public final double[] recentTps = new double[4];
    // CraftBukkit start
    public final java.util.concurrent.ExecutorService chatExecutor = java.util.concurrent.Executors.newCachedThreadPool(
            new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon(true).setNameFormat("Async Chat Thread - #%d").build());
    private final Object stopLock = new Object();
    // @formatter:off
    @Shadow public MinecraftServer.ReloadableResources resources;
    @Shadow public Map<ResourceKey<net.minecraft.world.level.Level>, ServerLevel> levels;
    @Shadow
    public ServerConnectionListener connection;
    @Shadow
    public WorldData worldData;
    @Shadow
    @Final
    public Executor executor;
    @Shadow
    @Final
    public ChunkProgressListenerFactory progressListenerFactory;
    // CraftBukkit start
    public WorldLoader.DataLoadContext worldLoader;
    public CraftServer server;
    public OptionSet options;
    public ConsoleCommandSender console;
    public ConsoleReader reader;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static int currentTick = (int) (System.currentTimeMillis() / 50);
    public Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
    public int autosavePeriod;
    public Commands vanillaCommandDispatcher;
    @Shadow private int tickCount;
    @Shadow
    private long nextTickTimeNanos;
    private boolean forceTicks;
    private boolean hasStopped = false;
    private static final int SAMPLE_INTERVAL = 20; // Paper

    @Unique
    private ServerLinks serverLinksVanilla = ServerLinks.EMPTY;
    public MixinMinecraftServer(String string) {
        super(string);
    }

    @Shadow
    private static void setInitialSpawn(ServerLevel serverLevel, ServerLevelData serverLevelData, boolean bl, boolean bl2) {
    }

    private static double calcTps(double avg, double exp, double tps) {
        return (avg * exp) + (tps * (1 - exp));
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static MinecraftServer getServer() {
        return Bukkit.getServer() instanceof CraftServer ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }
    // CraftBukkit end

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static RegistryAccess getDefaultRegistryAccess() {
        return CraftRegistry.getMinecraftRegistry();
    }

    @Inject(method = "setInitialSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;getGenerator()Lnet/minecraft/world/level/chunk/ChunkGenerator;", shift = At.Shift.BEFORE), cancellable = true)
    private static void taiyitist$spawnInit(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug, CallbackInfo ci) {
        // CraftBukkit start
        if (level.bridge$generator() != null) {
            Random rand = new Random(level.getSeed());
            org.bukkit.Location spawn = level.bridge$generator().getFixedSpawnLocation(level.getWorld(), rand);

            if (spawn != null) {
                if (spawn.getWorld() != level.getWorld()) {
                    throw new IllegalStateException("Cannot set spawn point for " + levelData.getLevelName() + " to be in another world (" + spawn.getWorld().getName() + ")");
                } else {
                    levelData.setSpawn(new BlockPos(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()), spawn.getYaw());
                    ci.cancel();
                }
            }
        }
    }

    @ModifyConstant(method = "spin", constant = @Constant(intValue = 8))
    private static int taiyitist$configurePriority(int constant) {
        return TaiyitistConfigUtil.serverThread();
    }

    @Shadow public abstract boolean isSpawningMonsters();
    @Shadow public abstract PlayerList getPlayerList();

    @Shadow public abstract boolean isStopped();

    @Shadow
    public abstract ServerLevel overworld();
    // Paper End

    @Shadow
    protected abstract void setupDebugLevel(WorldData worldData);

    @Shadow
    public abstract Set<ResourceKey<net.minecraft.world.level.Level>> levelKeys();

    @Shadow
    public abstract void executeIfPossible(Runnable task);

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Shadow
    @Nullable
    public abstract ServerLevel getLevel(ResourceKey<net.minecraft.world.level.Level> dimension);

    @Shadow public abstract GameRules getGameRules();@Shadow@Final private static long PREPARE_LEVELS_DEFAULT_DELAY_NANOS;@Shadow protected abstract void waitUntilNextTick();@Shadow protected abstract void updateMobSpawningFlags();@Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$loadOptions(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        OVERLOADED_THRESHOLD_NANOS = 30L * TimeUtil.NANOSECONDS_PER_SECOND / 20L; // CraftBukkit
        String[] arguments = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        OptionParser parser = new Main();
        try {
            options = parser.parse(arguments);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }
        Main.handleParser(parser, options);
        this.vanillaCommandDispatcher = worldStem.dataPackResources().getCommands();
        this.worldLoader = BukkitSnapshotCaptures.getDataLoadContext();

        com.taiyitistmc.fabric.LogManagerShutdownThread.unhook(); // Taiyitist - Improved watchdog support
        Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread(((MinecraftServer) (Object) this)));
        // CraftBukkit end
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", remap = false, ordinal = 0, shift = At.Shift.AFTER, target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    public void taiyitist$unloadPlugins(CallbackInfo ci) {
        if (this.server != null) {
            this.server.disablePlugins();
        }
    }

    @Decorate(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    private ServerStatus taiyitist$initTickParam(MinecraftServer instance, @Local(allocate = "tickSection") long tickSection, @Local(allocate = "tickCount") long tickCount) throws Throwable {
        var serverStatus = (ServerStatus) DecorationOps.callsite().invoke(instance);
        Arrays.fill(recentTps, 20);
        tickSection = Util.getMillis();
        tickCount = 1;
        DecorationOps.blackhole().invoke(tickSection, tickCount);
        return serverStatus;
    }

    @Decorate(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startMeasuringTaskExecutionTime()V"))
    private void taiyitist$updateTickParam(MinecraftServer instance, @Local(allocate = "tickSection") long tickSection, @Local(allocate = "tickCount") long tickCount) throws Throwable {
        if (tickCount++ % SAMPLE_INTERVAL == 0) {
            long curTime = Util.getMillis();
            double currentTps = 1E3 / (curTime - tickSection) * SAMPLE_INTERVAL;
            recentTps[0] = calcTps(recentTps[0], 0.92, currentTps); // 1/exp(5sec/1min)
            recentTps[1] = calcTps(recentTps[1], 0.9835, currentTps); // 1/exp(5sec/5min)
            recentTps[2] = calcTps(recentTps[2], 0.9945, currentTps); // 1/exp(5sec/15min)
            tickSection = curTime;
        }
        DecorationOps.blackhole().invoke(tickSection, tickCount);
        currentTick = (int) (System.currentTimeMillis() / 50);
        DecorationOps.callsite().invoke(instance);
    }

    @WrapWithCondition(method = "runServer", at = @At(value = "INVOKE", remap = false, target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private boolean taiyitist$warnOnLoad(org.slf4j.Logger instance, String s, Object o1, Object o2) throws Throwable {
        return server.getWarnOnOverload();
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"))
    private void taiyitist$watchdogExit(CallbackInfo ci) {
        WatchdogThread.doStop();
    }

    @Inject(method = "stopServer", at = @At("HEAD"), cancellable = true)
    private void taiyitist$stop(CallbackInfo ci) {
        synchronized (stopLock) {
            if (hasStopped) ci.cancel();
            hasStopped = true;
        }
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;removeAll()V"))
    private void taiyitist$stopThread(CallbackInfo ci) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        } // CraftBukkit - SPIGOT-625 - give server at least a chance to send packets
    }

    @Inject(method = "loadLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;prepareLevels(Lnet/minecraft/server/level/progress/ChunkProgressListener;)V",
            shift = At.Shift.AFTER))
    private void taiyitist$loadLevel(CallbackInfo ci) {
        if (!TaiyitistConfig.skipOtherWorldPreparing) {
            for (ServerLevel worldserver : ((MinecraftServer) (Object) this).getAllLevels()) {
                if (worldserver != overworld()) {
                    if (taiyitist$isNether(worldserver) && Bukkit.getAllowNether()) {
                        taiyitist$prepareWorld(worldserver);
                    } else if (taiyitist$isEnd(worldserver) && this.server.getAllowEnd()) {
                        taiyitist$prepareWorld(worldserver);
                    }
                    if (taiyitist$isNotNetherAndEnd(worldserver)) {
                        taiyitist$prepareWorld(worldserver);
                    }
                }
            }
        }
    }

    private boolean taiyitist$isNotNetherAndEnd(ServerLevel worldserver) {
        return !taiyitist$isNether(worldserver) && !taiyitist$isEnd(worldserver);
    }

    private boolean taiyitist$isNether(ServerLevel worldserver) {
        return worldserver == this.getLevel(net.minecraft.world.level.Level.NETHER);
    }

    private boolean taiyitist$isEnd(ServerLevel worldserver) {
        return worldserver == this.getLevel(net.minecraft.world.level.Level.END);
    }

    private void taiyitist$prepareWorld(ServerLevel worldserver) {
        this.prepareLevels(worldserver.getChunkSource().chunkMap.progressListener, worldserver);
        worldserver.entityManager.tick(); // SPIGOT-6526: Load pending entities so they are available to the API
        this.server.getPluginManager().callEvent(new WorldLoadEvent(worldserver.getWorld()));
    }

    @Inject(method = "loadLevel", at = @At("RETURN"))
    public void taiyitist$enablePlugins(CallbackInfo ci) {
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
        this.connection.acceptConnections();
    }

    @Inject(method = "createLevels", at = @At(value = "INVOKE", remap = false,
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private void taiyitist$worldInit(ChunkProgressListener listener, CallbackInfo ci,
                                  @com.llamalad7.mixinextras.sugar.Local ServerLevel serverLevel) {
        taiyitist$initLevel(serverLevel);
    }

    @Redirect(method = "createLevels",
            at = @At(value = "NEW", args = "class=net/minecraft/server/level/ServerLevel", ordinal = 1))
    private ServerLevel taiyitist$resetListener(MinecraftServer server, Executor dispatcher,
                                             LevelStorageSource.LevelStorageAccess levelStorageAccess,
                                             ServerLevelData serverLevelData, ResourceKey dimension,
                                             LevelStem levelStem, ChunkProgressListener progressListener,
                                             boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime,
                                             RandomSequences randomSequences) {
        ChunkProgressListener listener = this.progressListenerFactory.create(11);
        return new ServerLevel(server, dispatcher, levelStorageAccess, serverLevelData,
                dimension, levelStem, listener, isDebug, biomeZoomSeed, customSpawners, tickTime, randomSequences);
    }

    @Inject(method = "createLevels",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1)
    )
    private void taiyitist$initWorld(ChunkProgressListener chunkProgressListener, CallbackInfo ci,
                                  @com.llamalad7.mixinextras.sugar.Local WorldOptions worldOptions,
                                  @com.llamalad7.mixinextras.sugar.Local DerivedLevelData derivedLevelData,
                                  @com.llamalad7.mixinextras.sugar.Local(ordinal = 1) ServerLevel serverLevel2) {
        taiyitist$initLevel(serverLevel2);
        taiyitist$initializedLevel(serverLevel2, derivedLevelData, worldData, worldOptions);
    }

    @Inject(method = "getServerModName", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void taiyitist$setServerModName(CallbackInfoReturnable<String> cir) {
        if (this.server != null) {
            cir.setReturnValue(server.getName());
        }
    }

    @Override
    public boolean hasStopped() {
        synchronized (stopLock) {
            return hasStopped;
        }
    }

    @Override
    public void taiyitist$setServer(CraftServer server) {
        this.server = server;
    }

    @Override
    public void addLevel(ServerLevel level) {
        ServerWorldEvents.LOAD.invoker().onWorldLoad(((MinecraftServer) (Object) this), level);
        this.levels.put(level.dimension(), level);
    }

    @Override
    public void removeLevel(ServerLevel level) {
        ServerWorldEvents.UNLOAD.invoker().onWorldUnload(((MinecraftServer) (Object) this), level); // Banner
        this.levels.remove(level.dimension());
    }

    @Override
    public void initWorld(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
        taiyitist$initLevel(serverWorld);
        WorldBorder worldborder = serverWorld.getWorldBorder();
        worldborder.applySettings(worldInfo.getWorldBorder());
        taiyitist$initializedLevel(serverWorld, worldInfo, saveData, worldOptions);
    }

    private void taiyitist$initLevel(ServerLevel serverWorld) {
        this.server.scoreboardManager = new CraftScoreboardManager((MinecraftServer) (Object) this, serverWorld.getScoreboard());

        if (serverWorld.bridge$generator() != null) {
            serverWorld.getWorld().getPopulators().addAll(
                    serverWorld.bridge$generator().getDefaultPopulators(
                            serverWorld.getWorld()));
        }
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(serverWorld.getWorld()));
    }

    private void taiyitist$initializedLevel(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
        boolean flag = saveData.isDebugWorld();

        if (!worldInfo.isInitialized()) {
            try {
                setInitialSpawn(serverWorld, worldInfo, worldOptions.generateBonusChest(), flag);
                worldInfo.setInitialized(true);
                if (flag) {
                    this.setupDebugLevel(this.worldData);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception initializing level");
                try {
                    serverWorld.fillReportDetails(crashreport);
                } catch (Throwable throwable2) {
                    // empty catch block
                }
                throw new ReportedException(crashreport);
            }
            worldInfo.setInitialized(true);
        }
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public final void prepareLevels(ChunkProgressListener listener) {
        ServerLevel serverLevel = this.overworld();
        this.forceTicks = true;
        LOGGER.info("Preparing start region for dimension {}", serverLevel.dimension().location());
        BlockPos blockPos = serverLevel.getSharedSpawnPos();
        listener.updateSpawnPos(new ChunkPos(blockPos));
        ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
        this.nextTickTimeNanos = Util.getNanos();
        serverLevel.setDefaultSpawnPos(blockPos, serverLevel.getSharedSpawnAngle());
        int i = this.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS);
        int j = i > 0 ? Mth.square(ChunkProgressListener.calculateDiameter(i)) : 0;

        while(serverChunkCache.getTickingGenerated() < j) {
            this.executeModerately();
        }

        this.executeModerately();

        for(ServerLevel serverLevel2: this.levels.values()){
            TicketStorage ticketStorage = serverLevel2.getDataStorage().get(TicketStorage.TYPE);
            if (ticketStorage != null) {
                ticketStorage.activateAllDeactivatedTickets();
            }
        }

        this.executeModerately();
        listener.stop();
        this.forceTicks = false;
    }

    @Override
    public void prepareLevels(ChunkProgressListener listener, ServerLevel worldserver) {
        ServerWorldEvents.LOAD.invoker().onWorldLoad(((MinecraftServer) (Object) this), worldserver);// Taiyitist
        this.forceTicks = true;
        LOGGER.info("Preparing start region for dimension {}", worldserver.dimension().location());
        BlockPos blockPos = worldserver.getSharedSpawnPos();
        listener.updateSpawnPos(new ChunkPos(blockPos));
        ServerChunkCache serverChunkCache = worldserver.getChunkSource();
        this.nextTickTimeNanos = Util.getNanos();
        worldserver.setDefaultSpawnPos(blockPos, worldserver.getSharedSpawnAngle());
        int i = worldserver.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS);
        int j = i > 0 ? Mth.square(ChunkProgressListener.calculateDiameter(i)) : 0;

        while(serverChunkCache.getTickingGenerated() < j) {
            // CraftBukkit start
            // this.nextTickTimeNanos = SystemUtils.getNanos() + MinecraftServer.PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
            this.executeModerately();
        }

        // this.nextTickTimeNanos = SystemUtils.getNanos() + MinecraftServer.PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
        this.executeModerately();

        if(true){
            ServerLevel serverLevel2 = worldserver;
            TicketStorage ticketStorage = (TicketStorage)serverLevel2.getDataStorage().get(TicketStorage.TYPE);
            if (ticketStorage != null) {
                ticketStorage.activateAllDeactivatedTickets();
            }
        }

        // CraftBukkit start
        // this.nextTickTimeNanos = SystemUtils.getNanos() + MinecraftServer.PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
        this.executeModerately();
        // CraftBukkit end
        listener.stop();
        worldserver.setSpawnSettings(this.isSpawningMonsters());
        this.forceTicks = false;
        // CraftBukkit end
    }

    @Override
    public void executeModerately() {
        this.runAllTasks();
        this.bridge$drainQueuedTasks();
        java.util.concurrent.locks.LockSupport.parkNanos("executing tasks", 1000L);
    }

    @Inject(method = "haveTime", cancellable = true, at = @At("HEAD"))
    private void taiyitist$forceAheadOfTime(CallbackInfoReturnable<Boolean> cir) {
        if (this.forceTicks) cir.setReturnValue(true);
    }

    @Inject(method = "tickServer", at = @At("RETURN"))
    private void taiyitist$watchdogThreadStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        WatchdogThread.tick(); // Spigot
    }

    @Inject(method = "tickChildren", at = @At("HEAD"))
    private void taiyitist$processStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        BukkitFieldHooks.setCurrentTick((int) (System.currentTimeMillis() / 50));
        server.getScheduler().mainThreadHeartbeat();
        this.bridge$drainQueuedTasks();
    }

    @Inject(method = "tickChildren",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
                    ordinal = 0))
    private void taiyitist$mainThreadHeartbeat0(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.server.getScheduler().mainThreadHeartbeat(); // CraftBukkit
    }

    @Inject(method = "tickChildren",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/ServerFunctionManager;tick()V"))
    private void taiyitist$mainThreadHeartbeat1(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.server.getScheduler().mainThreadHeartbeat(); // CraftBukkit
    }

    @Inject(method = "tickChildren",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 0))
    private void taiyitist$mainThreadHeartbeat2(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.server.getScheduler().mainThreadHeartbeat(); // CraftBukkit
    }

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"))
    private void taiyitist$checkHeart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        // CraftBukkit start
        // Run tasks that are waiting on processing
        while (!processQueue.isEmpty()) {
            processQueue.remove().run();
        }

        // Send time updates to everyone, it will get the right time from the world the player is in.
        if (this.tickCount % 20 == 0) {
            for (int i = 0; i < this.getPlayerList().players.size(); ++i) {
                ServerPlayer entityplayer = this.getPlayerList().players.get(i);
                entityplayer.connection.send(new ClientboundSetTimePacket(entityplayer.level().getGameTime(), entityplayer.getPlayerTime(), entityplayer.level().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT))); // Add support for per player time
            }
        }
    }

    @Inject(method = "method_29440", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/packs/repository/PackRepository;setSelected(Ljava/util/Collection;)V"))
    private void taiyitist$syncCommands(Collection collection, MinecraftServer.ReloadableResources reloadableResources,
                                     CallbackInfo ci) {
        this.server.syncCommands(); // SPIGOT-5884: Lost on reload
    }

    // Banner start
    @Override
    public WorldLoader.DataLoadContext bridge$worldLoader() {
        return worldLoader;
    }

    @Override
    public CraftServer bridge$server() {
        return server;
    }

    @Override
    public OptionSet bridge$options() {
        return options;
    }

    @Override
    public ConsoleCommandSender bridge$console() {
        return console;
    }

    @Override
    public ConsoleReader bridge$reader() {
        return reader;
    }

    @Override
    public boolean bridge$forceTicks() {
        return forceTicks;
    }

    @Override
    public boolean isDebugging() {
        return false;
    }

    // Banner end

    @Override
    public void taiyitist$setConsole(ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public void bridge$drainQueuedTasks() {
        while (!processQueue.isEmpty()) {
            processQueue.remove().run();
        }
    }

    @Override
    public void bridge$queuedProcess(Runnable runnable) {
        processQueue.add(runnable);
    }

    @Override
    public Queue<Runnable> bridge$processQueue() {
        return processQueue;
    }

    @Override
    public void taiyitist$setProcessQueue(Queue<Runnable> processQueue) {
        this.processQueue = processQueue;
    }

    @Override
    public Commands bridge$getVanillaCommands() {
        return this.vanillaCommandDispatcher;
    }

    @Override
    public java.util.concurrent.ExecutorService bridge$chatExecutor() {
        return chatExecutor;
    }

    @Override
    public boolean isSameThread() {
        return super.isSameThread() || this.isStopped(); // CraftBukkit - MC-142590
    }

    @Override
    public double[] getTPS() {
        return recentTps;
    }

    @Override
    public void setServerLinks(ServerLinks serverLinks) {
        this.serverLinksVanilla = serverLinks;
    }

    @ModifyReturnValue(method = "serverLinks", at = @At("RETURN"))
    private ServerLinks taiyitist$resetServerLinks(ServerLinks original) {
        return serverLinksVanilla;
    }

    @Override
    public void handleCustomClickAction(ResourceLocation minecraftkey, Optional<Tag> optional, ServerPlayer player) {
        LOGGER.debug("Received custom click action {} with payload {}", minecraftkey, optional.orElse((Tag)null));
        CraftEventFactory.callPlayerCustomClickEvent(minecraftkey, optional, player);
    }

    @Override
    public int bridge$autosavePeriod() {
        return autosavePeriod;
    }

    @Override
    public void taiyitist$setAutosavePeriod(int autosavePeriod) {
        this.autosavePeriod = autosavePeriod;
    }



    @Inject(at = @At("HEAD"), method = "getServerModName", remap=false, cancellable = true)
    public void getServerModName_cardboard(CallbackInfoReturnable<String> ci) {
        if (null != Bukkit.getServer())
            ci.setReturnValue("xiaofan开发服务器核心1.21.8(fabric + bukkit)");
    }


}
