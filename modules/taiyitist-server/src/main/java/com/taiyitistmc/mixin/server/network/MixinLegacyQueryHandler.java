package com.taiyitistmc.mixin.server.network;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.network.LegacyQueryHandler;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.server.ServerListPingEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(LegacyQueryHandler.class)
public abstract class MixinLegacyQueryHandler {

    @Shadow @Final private ServerInfo server;

    private AtomicReference<ServerListPingEvent> taiyitist$pingEvent = new AtomicReference<>();

    @Inject(method = "channelRead", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;readableBytes()I"))
    private void taiyitist$pingEvent(ChannelHandlerContext channelHandlerContext, Object object, CallbackInfo ci, @Local SocketAddress socketAddress) {
        ServerListPingEvent event = CraftEventFactory.callServerListPingEvent(socketAddress, server.getMotd(), server.getPlayerCount(), server.getMaxPlayers()); // CraftBukkit
        taiyitist$pingEvent.set(event);
    }

    @Redirect(method = "channelRead", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/LegacyQueryHandler;createVersion0Response(Lnet/minecraft/server/ServerInfo;)Ljava/lang/String;"))
    private String taiyitist$addPingInfo(ServerInfo serverInfo) {
        return createVersion0Response(serverInfo, taiyitist$pingEvent.get() != null ? taiyitist$pingEvent.get() : null);
    }

    @Redirect(method = "channelRead", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/LegacyQueryHandler;createVersion1Response(Lnet/minecraft/server/ServerInfo;)Ljava/lang/String;"))
    private String taiyitist$addPingInfo0(ServerInfo serverInfo) {
        return createVersion1Response(serverInfo, taiyitist$pingEvent.get() != null ? taiyitist$pingEvent.get() : null);
    }

    private static String createVersion0Response(ServerInfo serverInfo, org.bukkit.event.server.ServerListPingEvent event) {
        return String.format(Locale.ROOT, "%s§%d§%d", event.getMotd(), event.getNumPlayers(), event.getMaxPlayers());
    }

    private static String createVersion1Response(ServerInfo serverInfo, org.bukkit.event.server.ServerListPingEvent event) {
        return String.format(Locale.ROOT, "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, serverInfo.getServerVersion(), event.getMotd(), event.getNumPlayers(), event.getMaxPlayers());
    }

}
