package com.taiyitistmc.mixin.server.network;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import com.taiyitistmc.injection.server.network.InjectionServerCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Taiyitist - TODO fix mixin
@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class MixinServerCommonPacketListenerImpl implements ServerCommonPacketListener, InjectionServerCommonPacketListenerImpl, CraftPlayer.TransferCookieConnection {

    @Shadow @Final private boolean transferred;

    @Shadow public abstract void send(Packet<?> packet);
    @Shadow public abstract void disconnect(Component component);

    @Shadow @Final protected Connection connection;
    protected ServerPlayer player;
    protected org.bukkit.craftbukkit.CraftServer cserver;
    public boolean processedDisconnect;

    // CraftBukkit start
    private static final ResourceLocation CUSTOM_REGISTER = ResourceLocation.withDefaultNamespace("register");
    private static final ResourceLocation CUSTOM_UNREGISTER = ResourceLocation.withDefaultNamespace("unregister");

    @ShadowConstructor
    public abstract void taiyitist$this(MinecraftServer server, Connection connection, CommonListenerCookie cookie);

    @CreateConstructor
    public void taiyitist$constructor(MinecraftServer server, Connection connection, CommonListenerCookie cookie, ServerPlayer player) {
        taiyitist$this(server, connection, cookie);
        this.player = player;
        this.player.taiyitist$setTransferCookieConnection(this);
        this.cserver = (CraftServer) Bukkit.getServer();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(MinecraftServer server, Connection connection, CommonListenerCookie cookie, CallbackInfo ci) {
        this.cserver = ((CraftServer) Bukkit.getServer());
    }

    @Inject(method = "handleKeepAlive", at = @At("HEAD"))
    private void taiyitist$makeSureRunning(ServerboundKeepAlivePacket serverboundKeepAlivePacket, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(serverboundKeepAlivePacket, this, this.player.level()); // CraftBukkit
    }

    @Override
    public boolean bridge$processedDisconnect() {
        return processedDisconnect;
    }

    @Override
    public void taiyitist$setProcessedDisconnect(boolean processedDisconnect) {
        this.processedDisconnect = processedDisconnect;
    }

    @Override
    public CraftPlayer getCraftPlayer() {
        return (this.player == null) ? null : (CraftPlayer) this.player.getBukkitEntity();
        // CraftBukkit end
    }

    @Override
    public boolean isTransferred() {
        return this.transferred;
    }

    @Override
    public ConnectionProtocol getProtocol() {
        return protocol();
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        send(packet);
    }

    @Override
    public void kickPlayer(Component reason) {
        disconnect(reason);
    }

    @Override
    public final boolean isDisconnected() {
        return !this.player.bridge$joining() && !this.connection.isConnected();
    }

    @Override
    public ServerPlayer bridge$player() {
        return player;
    }

    @Override
    public void taiyitist$setPlayer(ServerPlayer player) {
        this.player = player;
        this.player.taiyitist$setTransferCookieConnection(this);
    }

    @Override
    public void disconnect(String s) {
        this.disconnect(Component.literal(s));
    }
}
