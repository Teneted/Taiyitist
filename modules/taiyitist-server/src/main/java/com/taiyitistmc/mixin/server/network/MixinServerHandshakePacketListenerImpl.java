package com.taiyitistmc.mixin.server.network;

import com.google.gson.Gson;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.spigotmc.SpigotConfig;
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
    private static final Gson gson = new Gson();
    private static final java.util.regex.Pattern HOST_PATTERN = java.util.regex.Pattern.compile("[0-9a-f\\.:]{0,45}");
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

    @Inject(method = "beginLogin", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/Connection;setupOutboundProtocol(Lnet/minecraft/network/ProtocolInfo;)V"))
    private void taiyitist$throttler(ClientIntentionPacket packet, boolean bl, CallbackInfo ci) {
        try {
            long currentTime = System.currentTimeMillis();
            long connectionThrottle = Bukkit.getServer().getConnectionThrottle();
            InetAddress address = ((InetSocketAddress) this.connection.getRemoteAddress()).getAddress();
            synchronized (throttleTracker) {
                if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.get(address) < connectionThrottle) {
                    throttleTracker.put(address, currentTime);
                    var component = Component.literal("Connection throttled! Please wait before reconnecting.");
                    this.connection.send(new ClientboundLoginDisconnectPacket(component));
                    this.connection.disconnect(component);
                    ci.cancel();
                    return;
                }
                throttleTracker.put(address, currentTime);
                ++throttleCounter;
                if (throttleCounter > 200) {
                    throttleCounter = 0;
                    throttleTracker.entrySet().removeIf(entry -> entry.getValue() > connectionThrottle);
                }
            }
        } catch (Throwable t) {
            LogManager.getLogger().debug("Failed to check connection throttle", t);
        }
    }
}
