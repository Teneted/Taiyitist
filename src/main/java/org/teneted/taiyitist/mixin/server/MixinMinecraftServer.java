package org.teneted.taiyitist.mixin.server;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.DataFixer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.jline.terminal.Terminal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.bukkit.BukkitFieldHooks;
import org.teneted.taiyitist.bukkit.BukkitSnapshotCaptures;
import org.teneted.taiyitist.injection.server.InjectionMinecraftServer;

import java.lang.management.ManagementFactory;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements CommandSource, ServerInfo, ChunkIOErrorReporter, InjectionMinecraftServer {

    @Mutable
    @Shadow
    @Final
    private static long OVERLOADED_THRESHOLD_NANOS;

    @Shadow
    public ServerConnectionListener connection;
    @Shadow
    public abstract boolean isStopped();

    // CraftBukkit start
    public WorldLoader.DataLoadContext worldLoader;
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public Terminal terminal;
    public static int currentTick = (int) (System.currentTimeMillis() / 50);
    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    public Commands vanillaCommandDispatcher;
    private boolean forceTicks;
    public final java.util.concurrent.ExecutorService chatExecutor = java.util.concurrent.Executors.newCachedThreadPool(
            new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon(true).setNameFormat("Async Chat Thread - #%d").build());
    private final Object stopLock = new Object();
    private boolean hasStopped = false;

    public MixinMinecraftServer(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }
    // CraftBukkit end

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

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$loadOptions(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, java.net.Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, CallbackInfo ci) {
        OVERLOADED_THRESHOLD_NANOS = 30L * TimeUtil.NANOSECONDS_PER_SECOND / 20L; // CraftBukkit
        String[] arguments = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        OptionParser parser = new Main();
        try {
            options = parser.parse(arguments);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, ex.getLocalizedMessage());
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


    @Inject(method = "loadLevel", at = @At("RETURN"))
    public void taiyitist$enablePlugins(CallbackInfo ci) {
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
        this.connection.acceptConnections();
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
    public void executeModerately() {
        this.runAllTasks();
        this.bridge$drainQueuedTasks();
        java.util.concurrent.locks.LockSupport.parkNanos("executing tasks", 1000L);
    }

    @Inject(method = "haveTime", cancellable = true, at = @At("HEAD"))
    private void taiyitist$forceAheadOfTime(CallbackInfoReturnable<Boolean> cir) {
        if (this.forceTicks) cir.setReturnValue(true);
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
    private void taiyitist$mainThreadHeartbeat0(BooleanSupplier haveTime, CallbackInfo ci) {
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
    public int bridge$autosavePeriod() {
        return autosavePeriod;
    }

    @Override
    public void taiyitist$setAutosavePeriod(int autosavePeriod) {
        this.autosavePeriod = autosavePeriod;
    }
}
