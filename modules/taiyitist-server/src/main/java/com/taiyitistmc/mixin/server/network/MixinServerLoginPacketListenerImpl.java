package com.taiyitistmc.mixin.server.network;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import com.taiyitistmc.injection.server.network.InjectionServerLoginPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginPacketListenerImpl implements InjectionServerLoginPacketListenerImpl, CraftPlayer.TransferCookieConnection {

    @Shadow @Final private Connection connection;

    @Shadow public abstract void disconnect(Component component);

    @Shadow @Final private boolean transferred;

    @Shadow @Final private MinecraftServer server;
    @Shadow private volatile ServerLoginPacketListenerImpl.State state;
    private ServerPlayer player; // CraftBukkit

    @Override
    public boolean isTransferred() {
        return this.transferred;
    }

    @Override
    public ConnectionProtocol getProtocol() {
        return ConnectionProtocol.LOGIN;
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        this.connection.send(packet);
    }

    @Override
    public void kickPlayer(Component reason) {
        disconnect(reason);
    }
    // CraftBukkit end

    // CraftBukkit start
    @Deprecated
    public void disconnect(String s) {
        disconnect(Component.literal(s));
    }
    // CraftBukkit end
    
    // CraftBukkit start
    @Override
    public void callPlayerPreLoginEvents(GameProfile gameprofile) throws Exception {
        String playerName = gameprofile.getName();
        java.net.InetAddress address = ((java.net.InetSocketAddress) connection.getRemoteAddress()).getAddress();
        java.util.UUID uniqueId = gameprofile.getId();
        final org.bukkit.craftbukkit.CraftServer server = this.server.bridge$server();

        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId, this.transferred);
        server.getPluginManager().callEvent(asyncEvent);

        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
            final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
            if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
            }
            Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                @Override
                protected PlayerPreLoginEvent.Result evaluate() {
                    server.getPluginManager().callEvent(event);
                    return event.getResult();
                }
            };

            this.server.bridge$processQueue().add(waitable);
            if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                disconnect(event.getKickMessage());
                return;
            }
        } else {
            if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                disconnect(asyncEvent.getKickMessage());
                return;
            }
        }
    }
    // CraftBukkit end

    @Redirect(method = "verifyLoginAndFinishConnectionSetup", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"))
    private Component taiyitist$resetLogin(PlayerList instance, SocketAddress socketAddress, GameProfile gameProfile) {
        return null;
    }

    @Inject(method = "verifyLoginAndFinishConnectionSetup", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"))
    private void taiyitist$setLoginPlayer(GameProfile gameProfile, CallbackInfo ci, @Local PlayerList playerList) {
        this.player = playerList.canPlayerLogin(((ServerLoginPacketListenerImpl) (Object) this), gameProfile); // CraftBukkit
        if (this.player != null) {
            if (this.player.getBukkitEntity().isAwaitingCookies()) {
                //this.state = LoginListener.EnumProtocolState.WAITING_FOR_COOKIES;
            } else {
                //this.postCookies(gameprofile);
            }
        }
    }
}
