package com.taiyitistmc.mixin.server.network;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.PlayerInfoChannelPayload;
import com.taiyitistmc.bukkit.QueryAnswerPayload;
import com.taiyitistmc.config.TaiyitistConfig;
import com.taiyitistmc.injection.server.network.InjectionServerLoginPacketListenerImpl;
import com.mojang.authlib.GameProfile;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.thread.BlockableEventLoop;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener, InjectionServerLoginPacketListenerImpl, CraftPlayer.TransferCookieConnection {

    @Shadow
    @Final
    private static AtomicInteger UNIQUE_THREAD_ID;
    @Shadow
    @Final
    static Logger LOGGER;
    @Shadow
    @Final
    public Connection connection;
    @Shadow
    @Final
    MinecraftServer server;
    @Shadow
    @Nullable String requestedUsername;
    private ServerPlayer player; // CraftBukkit

    @Shadow
    public abstract void disconnect(Component component);

    @Shadow
    abstract void startClientVerification(GameProfile gameProfile);

    @Shadow @Nullable private GameProfile authenticatedProfile;
    private int velocityLoginMessageId = -1; // Paper - Add Velocity IP Forwarding Support

    @Inject(method = "handleLoginAcknowledgement", at = @At("HEAD"))
    private void taiyitist$ensureThread(ServerboundLoginAcknowledgedPacket packet, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(packet, this, this.server); // CraftBukkit
    }

    @Inject(method = "handleLoginAcknowledgement", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupInboundProtocol(Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/PacketListener;)V"))
    private void taiyitist$setPlayer(ServerboundLoginAcknowledgedPacket p_298815_, CallbackInfo ci, @Local ServerConfigurationPacketListenerImpl listener) {
        listener.taiyitist$setPlayer(this.player);
    }

    @Redirect(method = "verifyLoginAndFinishConnectionSetup", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"))
    private Component taiyitist$canLogin(PlayerList instance, SocketAddress socketAddress, GameProfile gameProfile, @Local PlayerList playerList) {
        if (this.player == null) {
            this.player = playerList.taiyitist$canPlayerLogin(socketAddress, gameProfile, (ServerLoginPacketListenerImpl) (Object) this);
        }
        return null;
    }

    @Override
    public void disconnect(final String s) {
        this.disconnect(Component.literal(s));
    }

    @Redirect(method = "handleHello",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V",
                    ordinal = 1))
    private void taiyitist$handleHello(ServerLoginPacketListenerImpl instance, GameProfile gameProfile, @Cancellable CallbackInfo ci) {
        // Paper start - Add Velocity IP Forwarding Support
        if (TaiyitistConfig.velocityEnabled) {
            this.velocityLoginMessageId = java.util.concurrent.ThreadLocalRandom.current().nextInt();
            net.minecraft.network.FriendlyByteBuf buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
            buf.writeByte(com.destroystokyo.paper.proxy.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION);
            net.minecraft.network.protocol.login.ClientboundCustomQueryPacket packet1 = new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(this.velocityLoginMessageId, new PlayerInfoChannelPayload(com.destroystokyo.paper.proxy.VelocityProxy.PLAYER_INFO_CHANNEL, buf));
            this.connection.send(packet1);
            ci.cancel();
            return;
        }
        // Paper end - Add Velocity IP Forwarding Support
        // CraftBukkit start
        class Handler extends Thread {

            public Handler() {
                super("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet());
            }

            @Override
            public void run() {
                try {
                    GameProfile gameprofile = UUIDUtil.createOfflineProfile(requestedUsername);

                    callPlayerPreLoginEvents(gameprofile);
                    LOGGER.info("UUID of player {} is {}", gameprofile.getName(), gameprofile.getId());
                    startClientVerification(gameprofile);
                } catch (Exception ex) {
                    disconnect("Failed to verify username!");
                    server.bridge$server().getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + requestedUsername, ex);
                }
            }
        }
        Handler thread = new Handler();
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
        // CraftBukkit end
    }


    // CraftBukkit start
    @Override
    public void callPlayerPreLoginEvents(GameProfile gameprofile) throws Exception {
        // Paper start - Add Velocity IP Forwarding Support
        if (this.velocityLoginMessageId == -1 && TaiyitistConfig.velocityEnabled) {
            disconnect("This server requires you to connect with Velocity.");
            return;
        }
        // Paper end - Add Velocity IP Forwarding Support
        String playerName = gameprofile.getName();
        java.net.InetAddress address = ((java.net.InetSocketAddress) connection.getRemoteAddress()).getAddress();
        java.util.UUID uniqueId = gameprofile.getId();
        final CraftServer server = this.server.bridge$server();

        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
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
            }
        } else {
            if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                disconnect(asyncEvent.getKickMessage());
            }
        }
    }
    // CraftBukkit end

    @Inject(method = "handleCookieResponse", at = @At("HEAD"), cancellable = true)
    private void taiyitist$handleCookie(ServerboundCookieResponsePacket serverboundCookieResponsePacket, CallbackInfo ci) {
        // CraftBukkit start
        PacketUtils.ensureRunningOnSameThread(serverboundCookieResponsePacket, this, (BlockableEventLoop) this.server);
        if (this.player.getBukkitEntity().handleCookieResponse(serverboundCookieResponsePacket)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void taiyitist$supportVelocity(ServerboundCustomQueryAnswerPacket packet, CallbackInfo ci) {
        // Paper start - Add Velocity IP Forwarding Support
        if (TaiyitistConfig.velocityEnabled && packet.transactionId() == this.velocityLoginMessageId) {
            if (packet.payload() instanceof QueryAnswerPayload payload) {
                if (payload == null) {
                    this.disconnect("This server requires you to connect with Velocity.");
                    ci.cancel();
                    return;
                }

                net.minecraft.network.FriendlyByteBuf buf = payload.buffer;

                if (!com.destroystokyo.paper.proxy.VelocityProxy.checkIntegrity(buf)) {
                    this.disconnect("Unable to verify player details");
                    ci.cancel();
                    return;
                }

                int version = buf.readVarInt();
                if (version > com.destroystokyo.paper.proxy.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION) {
                    throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted upto " + com.destroystokyo.paper.proxy.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION);
                }

                java.net.SocketAddress listening = this.connection.getRemoteAddress();
                int port = 0;
                if (listening instanceof java.net.InetSocketAddress) {
                    port = ((java.net.InetSocketAddress) listening).getPort();
                }
                this.connection.address = new java.net.InetSocketAddress(com.destroystokyo.paper.proxy.VelocityProxy.readAddress(buf), port);

                this.authenticatedProfile = com.destroystokyo.paper.proxy.VelocityProxy.createProfile(buf);

                //TODO Update handling for lazy sessions, might not even have to do anything?

                // Proceed with login
                class Handler extends Thread {

                    public Handler() {
                        super("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet());
                    }

                    @Override
                    public void run() {
                        try {
                            GameProfile gameprofile = UUIDUtil.createOfflineProfile(requestedUsername);

                            callPlayerPreLoginEvents(gameprofile);
                            LOGGER.info("UUID of player {} is {}", gameprofile.getName(), gameprofile.getId());
                            startClientVerification(gameprofile);
                        } catch (Exception ex) {
                            disconnect("Failed to verify username!");
                            server.bridge$server().getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + requestedUsername, ex);
                        }
                    }
                }
                Handler thread = new Handler();
                thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
                thread.start();
                ci.cancel();
                return;
            }
            // Paper end - Add Velocity IP Forwarding Support
        }
    }
}