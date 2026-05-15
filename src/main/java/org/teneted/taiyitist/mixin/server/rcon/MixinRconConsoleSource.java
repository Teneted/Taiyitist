package org.teneted.taiyitist.mixin.server.rcon;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.rcon.RconConsoleSource;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.taiyitist.asm.annotation.CreateConstructor;
import org.teneted.taiyitist.asm.annotation.ShadowConstructor;
import org.teneted.taiyitist.injection.server.rcon.InjectionRconConsoleSource;

import java.net.SocketAddress;

@Mixin(RconConsoleSource.class)
public class MixinRconConsoleSource implements InjectionRconConsoleSource {

    // CraftBukkit start
    public SocketAddress socketAddress;
    @Shadow
    @Final
    private StringBuffer buffer;
    private CraftRemoteConsoleCommandSender remoteConsole = null;

    @ShadowConstructor
    public void taiyitist$constructor(MinecraftServer pServer) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(MinecraftServer pServer, SocketAddress socketAddress) {
        taiyitist$constructor(pServer);
        this.socketAddress = socketAddress;
    }

    @Override
    public SocketAddress bridge$socketAddress() {
        return socketAddress;
    }

    @Override
    public void taiyitist$setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public void sendMessage(String message) {
        this.buffer.append(message);
    }

    @Override
    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        if (remoteConsole == null) {
            remoteConsole = new CraftRemoteConsoleCommandSender((RconConsoleSource) (Object) this);
        }
        return this.remoteConsole;
    }
}
