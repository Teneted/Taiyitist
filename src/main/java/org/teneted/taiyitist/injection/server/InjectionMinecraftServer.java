package org.teneted.taiyitist.injection.server;

import joptsimple.OptionSet;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;

import java.util.Optional;

public interface InjectionMinecraftServer {

    default void handleCustomClickAction(Identifier minecraftkey, Optional<Tag> optional, ServerPlayer player) {
        throw new IllegalStateException("Not implemented");
    }

    default void bridge$drainQueuedTasks() {
        throw new IllegalStateException("Not implemented");
    }

    default Commands bridge$getVanillaCommands() {
        throw new IllegalStateException("Not implemented");
    }

    default java.util.concurrent.ExecutorService bridge$chatExecutor() {
        throw new IllegalStateException("Not implemented");
    }

    default void bridge$queuedProcess(Runnable runnable) {
        throw new IllegalStateException("Not implemented");
    }

    default java.util.Queue<Runnable> bridge$processQueue() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setProcessQueue(java.util.Queue<Runnable> processQueue) {
        throw new IllegalStateException("Not implemented");
    }

    default WorldLoader.DataLoadContext bridge$worldLoader() {
        throw new IllegalStateException("Not implemented");
    }

    default CraftServer bridge$server() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setServer(CraftServer server) {
        throw new IllegalStateException("Not implemented");
    }

    default OptionSet bridge$options() {
        throw new IllegalStateException("Not implemented");
    }

    default ConsoleCommandSender bridge$console() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setConsole(ConsoleCommandSender console) {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setRemoteConsole(RemoteConsoleCommandSender remoteConsole) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$forceTicks() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isDebugging() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean hasStopped() {
        throw new IllegalStateException("Not implemented");
    }

    default void initWorld(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
        throw new IllegalStateException("Not implemented");
    }

    default void prepareLevels(ServerLevel serverWorld) {
        throw new IllegalStateException("Not implemented");
    }

    default void addLevel(ServerLevel level) {
        throw new IllegalStateException("Not implemented");
    }

    default void removeLevel(ServerLevel level) {
        throw new IllegalStateException("Not implemented");
    }

    default void executeModerately() {
        throw new IllegalStateException("Not implemented");
    }

    default double[] getTPS() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setRconConsoleSource(RconConsoleSource source) {
        throw new IllegalStateException("Not implemented");
    }

    default void setServerLinks(ServerLinks serverLinks) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$autosavePeriod() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setAutosavePeriod(int autosavePeriod) {
        throw new IllegalStateException("Not implemented");
    }
}
