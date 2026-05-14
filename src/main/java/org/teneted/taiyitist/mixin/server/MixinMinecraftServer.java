package org.teneted.taiyitist.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.bukkit.BukkitSnapshotCaptures;
import org.teneted.taiyitist.injection.server.InjectionMinecraftServer;

import java.lang.management.ManagementFactory;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
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
    public abstract @Nullable ServerLevel getLevel(ResourceKey<Level> dimension);

    @Shadow
    public abstract ServerLevel overworld();

    @Shadow
    private static void setInitialSpawn(ServerLevel level, ServerLevelData levelData, boolean spawnBonusChest, boolean isDebug, LevelLoadListener levelLoadListener) {
    }

    @Shadow
    protected abstract void setupDebugLevel(WorldData worldData);

    @Shadow
    public WorldData worldData;
    @Shadow
    @Final
    public LevelLoadListener levelLoadListener;

    @Shadow
    public abstract void handleCustomClickAction(Identifier id, Optional<Tag> payload);

    // CraftBukkit start
    public WorldLoader.DataLoadContext worldLoader;
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public static int currentTick = (int) (System.currentTimeMillis() / 50);
    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    public Commands vanillaCommandDispatcher;
    private boolean forceTicks;
    // CraftBukkit end
    // CraftBukkit start
    public final java.util.concurrent.ExecutorService chatExecutor = java.util.concurrent.Executors.newCachedThreadPool(
            new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon(true).setNameFormat("Async Chat Thread - #%d").build());
    // CraftBukkit end

    public MixinMinecraftServer(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, CallbackInfo ci) {
        OVERLOADED_THRESHOLD_NANOS =30L * TimeUtil.NANOSECONDS_PER_SECOND / 20L; // CraftBukkit
        String[] arguments = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        OptionParser parser = new Main();
        try {
            options = parser.parse(arguments);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, ex.getLocalizedMessage());
        }
        Main.handleParser(parser, options);
        this.worldLoader = BukkitSnapshotCaptures.getDataLoadContext();
        this.vanillaCommandDispatcher = worldStem.dataPackResources().getCommands();
        Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread(((MinecraftServer) (Object) this)));
        // CraftBukkit end
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
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private void taiyitist$worldInit(CallbackInfo ci, @Local ServerLevel serverLevel) {
        taiyitist$initLevel(serverLevel);
    }

    @Override
    public void handleCustomClickAction(Identifier minecraftkey, Optional<Tag> optional, ServerPlayer player) {
        handleCustomClickAction(minecraftkey, optional);
        CraftEventFactory.callPlayerCustomClickEvent(minecraftkey, optional, player);
    }

    @Redirect(method = "createLevels",
            at = @At(value = "NEW", args = "class=net/minecraft/server/level/ServerLevel", ordinal = 1))
    private ServerLevel taiyitist$resetListener(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess levelStorage, ServerLevelData levelData, ResourceKey dimension, LevelStem levelStem, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime) {
        return new ServerLevel(server, executor, levelStorage, levelData, dimension, levelStem, isDebug, biomeZoomSeed, customSpawners, tickTime);
    }

    @Inject(method = "createLevels",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1)
    )
    private void taiyitist$initWorld(CallbackInfo ci, @Local(name = "worldOptions") WorldOptions worldOptions, @Local(name = "derivedLevelData") DerivedLevelData derivedLevelData, @Local(name = "level") ServerLevel serverLevel2) {
        taiyitist$initLevel(serverLevel2);
        taiyitist$initializedLevel(serverLevel2, derivedLevelData, worldData, worldOptions);
    }

    @Inject(method = "getServerModName", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void taiyitist$setServerModName(CallbackInfoReturnable<String> cir) {
        if (this.server != null) {
            cir.setReturnValue(server.getName());
        }
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


    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    @Deprecated
    private static MinecraftServer getServer() {
        return Bukkit.getServer() instanceof CraftServer ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }
    // CraftBukkit end

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    @Deprecated
    private static RegistryAccess getDefaultRegistryAccess() {
        return CraftRegistry.getMinecraftRegistry();
    }

    // CraftBukkit start
    @Override
    public boolean isDebugging() {
        return false;
    }
}
