package com.taiyitistmc.mixin.server.dedicated;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.taiyitistmc.TaiyitistMod;
import com.taiyitistmc.Metrics;
import com.taiyitistmc.config.TaiyitistConfig;
import com.taiyitistmc.util.I18n;
import com.mojang.datafixers.DataFixer;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.Logger;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.io.IoBuilder;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.util.ForwardLogHandler;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends MinecraftServer {

    public AtomicReference<RconConsoleSource> rconConsoleSource = new AtomicReference<>(null);

    public MixinDedicatedServer(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, levelStorageAccess, packRepository, worldStem, proxy, dataFixer, services, chunkProgressListenerFactory);
    }

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;usesAuthentication()Z", ordinal = 1))
    private void taiyitist$initServer(CallbackInfoReturnable<Boolean> cir) {
        TaiyitistMod.LOGGER.info(I18n.as("bukkit.plugin.loading.info"));
        // CraftBukkit start
        SpigotConfig.init((File) this.bridge$options().valueOf("spigot-settings"));
        TaiyitistConfig.init((File) this.bridge$options().valueOf("taiyitist-settings"));
        SpigotConfig.registerCommands();
        this.bridge$server().loadPlugins();
        this.bridge$server().enablePlugins(PluginLoadOrder.STARTUP);
    }

    @Inject(method = "initServer",
            at = @At(value = "INVOKE",
                    target = "Ljava/lang/Thread;setDaemon(Z)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    private void taiyitist$addLog4j(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - TODO: handle command-line logging arguments
        Logger global = Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new ForwardLogHandler());
        final org.apache.logging.log4j.Logger logger = LogManager.getRootLogger();

        System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(logger).setLevel(Level.WARN).buildPrintStream());
        // CraftBukkit end
    }

    @Inject(method = "getPluginNames", at = @At("RETURN"), cancellable = true)
    private void taiyitist$setPluginNames(CallbackInfoReturnable<String> cir) {
        StringBuilder result = new StringBuilder();
        Plugin[] plugins = bridge$server().getPluginManager().getPlugins();

        result.append(bridge$server().getName());
        result.append(" on Bukkit ");
        result.append(bridge$server().getBukkitVersion());

        if (plugins.length > 0 && bridge$server().getQueryPlugins()) {
            result.append(": ");

            for (int i = 0; i < plugins.length; i++) {
                if (i > 0) {
                    result.append("; ");
                }

                result.append(plugins[i].getDescription().getName());
                result.append(" ");
                result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
            }
        }

        cir.setReturnValue(result.toString());
    }

    @Redirect(method = "handleConsoleInputs", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void taiyitist$serverCommandEvent(Commands commands, CommandSourceStack source, String command) {
        if (command.isEmpty()) {
            return;
        }
        ServerCommandEvent event = new ServerCommandEvent(bridge$console(), command);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            bridge$server().dispatchServerCommand(bridge$console(), new ConsoleInput(event.getCommand(), source));
        }
    }

    @Override
    public void taiyitist$setRconConsoleSource(RconConsoleSource source) {
        rconConsoleSource.set(source);
    }

    /**
     * @author Mgazul
     * @reason
     */
    @Overwrite
    public String runCommand(String command) {
        RconConsoleSource rconConsoleSource1 = rconConsoleSource.get();
        rconConsoleSource1.prepareForCommand();
        this.executeBlocking(() -> {
            CommandSourceStack wrapper = rconConsoleSource1.createCommandSourceStack();
            RemoteServerCommandEvent event = new RemoteServerCommandEvent(rconConsoleSource1.getBukkitSender(wrapper), command);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            ConsoleInput serverCommand = new ConsoleInput(event.getCommand(), wrapper);
            this.bridge$server().dispatchServerCommand(event.getSender(), serverCommand);
        });
        return rconConsoleSource1.getCommandResponse();
    }

    @Inject(method = "onServerExit", at = @At("RETURN"))
    public void taiyitist$exitNow(CallbackInfo ci) {
        try {
            TerminalConsoleAppender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread exitThread = new Thread(this::taiyitist$exit, "Exit Thread");
        exitThread.setDaemon(true);
        exitThread.start();
    }

    private void taiyitist$exit() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> threads = new ArrayList<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (!thread.isDaemon() && !thread.getName().equals("DestroyJavaVM")) {
                threads.add(thread.getName());
            }
        }
        if (!threads.isEmpty()) {
            TaiyitistMod.LOGGER.debug("Threads {} not shutting down", String.join(", ", threads));
            TaiyitistMod.LOGGER.info("{} threads not shutting down correctly, force exiting", threads.size());
        }
        System.exit(0);
    }

    @Inject(method = "initServer", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V",
            remap = false,
            ordinal = 3, shift = At.Shift.AFTER))
    private void taiyitist$startMetrics(CallbackInfoReturnable<Boolean> cir) {
        Metrics.BannerMetrics.startMetrics();
    }

    @WrapWithCondition(method = "initServer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"),slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", ordinal = 5),
            to = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", ordinal = 6)))
    private boolean taiyitist$velocityCheck(org.slf4j.Logger instance, String string) {
        boolean usingProxy = org.spigotmc.SpigotConfig.bungee || TaiyitistConfig.velocityEnabled;
        return usingProxy;
    }

    @ModifyArg(method = "initServer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", ordinal = 5))
    private String taiyitist$resetProxy(String s) {
        String proxyFlavor = (TaiyitistConfig.velocityEnabled) ? "Velocity" : "BungeeCord";
        if (TaiyitistConfig.velocityEnabled) {
            return "Whilst this makes it possible to use " + proxyFlavor + ", unless access to your server is properly restricted, it also opens up the ability for hackers to connect with any username they choose.";
        }else {
            return "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.";
        }
    }

    @ModifyArg(method = "initServer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", ordinal = 6))
    private String taiyitist$resetProxy0(String s) {
        String proxyLink = (TaiyitistConfig.velocityEnabled) ? "https://docs.papermc.io/velocity/security" : "http://www.spigotmc.org/wiki/firewall-guide/";
        if (TaiyitistConfig.velocityEnabled) {
            return "Please see " + proxyLink + " for further information.";
        }else {
            return "To change this, set \"online-mode\" to \"true\" in the server.properties file.";
        }
    }
}
