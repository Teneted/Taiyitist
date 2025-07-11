package com.taiyitistmc.mixin.server.network;

import java.net.InetAddress;
import java.util.HashMap;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO fixed
@Mixin(ServerHandshakePacketListenerImpl.class)
public abstract class MixinServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {

    // CraftBukkit start - add fields
    private static final HashMap<InetAddress, Long> throttleTracker = new HashMap<InetAddress, Long>();
    // CraftBukkit end
    private static int throttleCounter = 0;
    @Shadow
    @Final
    private Connection connection;
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "handleIntention", at = @At("HEAD"))
    private void taiyitist$setHostName(ClientIntentionPacket packet, CallbackInfo ci) {
        this.connection.taiyitist$setHostName(packet.hostName() + ":" + packet.port()); // CraftBukkit  - set hostname
    }

    @Inject(method = "beginLogin", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/handshake/ClientIntentionPacket;protocolVersion()I", ordinal = 0))
    private void taiyitist$throttler(ClientIntentionPacket packet, boolean bl, CallbackInfo ci) {
        // CraftBukkit start - Connection throttle
        try {
            long currentTime = System.currentTimeMillis();
            long connectionThrottle = this.server.bridge$server().getConnectionThrottle();
            InetAddress address = ((java.net.InetSocketAddress) this.connection.getRemoteAddress()).getAddress();

            synchronized (throttleTracker) {
                if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.get(address) < connectionThrottle) {
                    throttleTracker.put(address, currentTime);
                    Component chatmessage = Component.literal("Connection throttled! Please wait before reconnecting.");
                    this.connection.send(new ClientboundLoginDisconnectPacket(chatmessage));
                    this.connection.disconnect(chatmessage);
                    ci.cancel();
                }

                throttleTracker.put(address, currentTime);
                throttleCounter++;
                if (throttleCounter > 200) {
                    throttleCounter = 0;

                    // Cleanup stale entries
                    java.util.Iterator iter = throttleTracker.entrySet().iterator();
                    while (iter.hasNext()) {
                        java.util.Map.Entry<InetAddress, Long> entry = (java.util.Map.Entry) iter.next();
                        if (entry.getValue() > connectionThrottle) {
                            iter.remove();
                        }
                    }
                }
            }
        } catch (Throwable t) {
            org.apache.logging.log4j.LogManager.getLogger().debug("Failed to check connection throttle", t);
        }
        // CraftBukkit end
    }
}
