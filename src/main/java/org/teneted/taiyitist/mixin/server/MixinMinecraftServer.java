package org.teneted.taiyitist.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.DataFixer;

import java.lang.management.ManagementFactory;
import java.net.Proxy;
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
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ChunkLoadCounter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelData;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.bukkit.BukkitFieldHooks;
import org.teneted.taiyitist.bukkit.BukkitSnapshotCaptures;
import org.teneted.taiyitist.injection.server.InjectionMinecraftServer;

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
    // CraftBukkit start
    public WorldLoader.DataLoadContext worldLoader;
    public CraftServer server;
    public OptionSet options;
    public ConsoleCommandSender console;
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
    private ServerLinks serverLinksVanilla = ServerLinks.EMPTY;public MixinMinecraftServer(String name, boolean propagatesCrashes) {
    super(name, propagatesCrashes);
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
    private static void taiyitist$spawnInit(ServerLevel level, ServerLevelData levelData, boolean spawnBonusChest, boolean isDebug, LevelLoadListener levelLoadListener, CallbackInfo ci) {
        // CraftBukkit start
        if (level.bridge$generator() != null) {
            Random rand = new Random(level.getSeed());
            org.bukkit.Location spawn = level.bridge$generator().getFixedSpawnLocation(level.getWorld(), rand);

            if (spawn != null) {
                if (spawn.getWorld() != level.getWorld()) {
                    throw new IllegalStateException("Cannot set spawn point for " + levelData.getLevelName() + " to be in another world (" + spawn.getWorld().getName() + ")");
                } else {
                    levelData.setSpawn(LevelData.RespawnData.of(level.dimension(), new BlockPos(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()), spawn.getYaw(), spawn.getPitch()));
                    ci.cancel();
                }
            }
        }
    }

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

    @Shadow public abstract GameRules getGameRules();
    @Shadow
    @Final private static long PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
    @Shadow protected abstract void waitUntilNextTick();
    @Shadow protected abstract void updateMobSpawningFlags();

    @Shadow protected  static void setInitialSpawn(ServerLevel level, ServerLevelData levelData, boolean spawnBonusChest, boolean isDebug, LevelLoadListener levelLoadListener){
    }

    @Shadow
    @Final public LevelLoadListener levelLoadListener;
    @Shadow public abstract int getAbsoluteMaxWorldSize();
    @Shadow protected abstract void updateEffectiveRespawnData();
    @Shadow public abstract Iterable<ServerLevel> getAllLevels();
    @Shadow private PlayerList playerList;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$loadOptions(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, CallbackInfo ci) {
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

        Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread(((MinecraftServer) (Object) this)));
        // CraftBukkit end
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", remap = false, ordinal = 0, shift = At.Shift.AFTER, target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    public void taiyitist$unloadPlugins(CallbackInfo ci) {
        if (this.server != null) {
            this.server.disablePlugins();
        }
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

    @Inject(method = "loadLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;prepareLevels()V",
            shift = At.Shift.AFTER))
    private void taiyitist$loadLevel(CallbackInfo ci) {
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
        this.prepareLevels(worldserver);
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
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void taiyitist$worldInit(CallbackInfo ci, @com.llamalad7.mixinextras.sugar.Local(name="overworld") ServerLevel overworld) {
        taiyitist$initLevel(overworld);
    }

    @Redirect(method = "createLevels",
            at = @At(value = "NEW", args = "class=net/minecraft/server/level/ServerLevel", ordinal = 1))
    private ServerLevel taiyitist$resetListener(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess levelStorage, ServerLevelData levelData, ResourceKey dimension, LevelStem levelStem, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime) {
        return new ServerLevel(server, executor, levelStorage, levelData,
                dimension, levelStem, isDebug, biomeZoomSeed, customSpawners, tickTime);
    }

    @Inject(method = "createLevels",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1)
    )
    private void taiyitist$initWorld(CallbackInfo ci, @com.llamalad7.mixinextras.sugar.Local(name="level") ServerLevel serverLevel2, @com.llamalad7.mixinextras.sugar.Local WorldOptions worldOptions, @com.llamalad7.mixinextras.sugar.Local DerivedLevelData derivedLevelData) {
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
        ServerLevelEvents.LOAD.invoker().onLevelLoad(((MinecraftServer) (Object) this), level);
        this.levels.put(level.dimension(), level);
    }

    @Override
    public void removeLevel(ServerLevel level) {
        ServerLevelEvents.UNLOAD.invoker().onLevelUnload(((MinecraftServer) (Object) this), level); // Banner
        this.levels.remove(level.dimension());
    }

    @Override
    public void initWorld(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
        taiyitist$initLevel(serverWorld);
        WorldBorder worldborder = serverWorld.getWorldBorder();
        initWorldBorder(serverWorld);
        taiyitist$initializedLevel(serverWorld, worldInfo, saveData, worldOptions);
    }

    private void initWorldBorder(ServerLevel serverlevel1) {
        serverlevel1.getWorldBorder().setAbsoluteMaxSize(this.getAbsoluteMaxWorldSize());
        this.getPlayerList().addWorldborderListener(serverlevel1);
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
                setInitialSpawn(serverWorld, worldInfo, worldOptions.generateBonusChest(), flag, this.levelLoadListener);
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
    public final void prepareLevels() {
        this.forceTicks = true;
        ChunkLoadCounter chunkLoadCounter = new ChunkLoadCounter();

        for(ServerLevel level : this.levels.values()) {
            chunkLoadCounter.track(level, () -> {
                TicketStorage savedTickets = (TicketStorage)level.getDataStorage().get(TicketStorage.TYPE);
                if (savedTickets != null) {
                    savedTickets.activateAllDeactivatedTickets();
                }

            });
        }

        this.levelLoadListener.start(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.totalChunks());

        do {
            this.levelLoadListener.update(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.readyChunks(), chunkLoadCounter.totalChunks());
            // CraftBukkit start
            // this.nextTickTimeNanos = Util.getNanos() + MinecraftServer.PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
            this.executeModerately();
            // CraftBukkit end
        } while(chunkLoadCounter.pendingChunks() > 0);

        this.levelLoadListener.finish(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS);
        // CraftBukkit start
        // this.updateMobSpawningFlags();

        this.forceTicks = false;
        // CraftBukkit end
        this.updateEffectiveRespawnData();
    }

    @Override
    public void prepareLevels(ServerLevel serverlevel) {
        this.forceTicks = true;
        ChunkLoadCounter chunkLoadCounter = new ChunkLoadCounter();

        { // CraftBukkit
            chunkLoadCounter.track(serverlevel, () -> {
                TicketStorage savedTickets = (TicketStorage)serverlevel.getDataStorage().get(TicketStorage.TYPE);
                if (savedTickets != null) {
                    savedTickets.activateAllDeactivatedTickets();
                }

            });
        }

        this.levelLoadListener.start(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.totalChunks());

        do {
            this.levelLoadListener.update(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.readyChunks(), chunkLoadCounter.totalChunks());
            // CraftBukkit start
            // this.nextTickTimeNanos = Util.getNanos() + MinecraftServer.PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
            this.executeModerately();
            // CraftBukkit end
        } while(chunkLoadCounter.pendingChunks() > 0);

        this.levelLoadListener.finish(LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS);
        // CraftBukkit start
        // this.updateMobSpawningFlags();

        this.forceTicks = false;
        // CraftBukkit end
        this.updateEffectiveRespawnData();
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

        // CraftBukkit start
        for (ServerLevel serverlevel : this.getAllLevels()) {
            this.playerList.broadcastAll(new ClientboundSetTimePacket(serverlevel.getGameTime(), Map.of()), serverlevel);
        }
        // CraftBukkit end
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
    public void handleCustomClickAction(Identifier minecraftkey, Optional<Tag> optional, ServerPlayer player) {
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
}
