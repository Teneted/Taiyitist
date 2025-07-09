package com.taiyitistmc.mixin.network;

import com.taiyitistmc.injection.network.connection.InjectionConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.SocketAddress;

@Mixin(Connection.class)
public class MixinConnection implements InjectionConnection {

    @Shadow
    public Channel channel;
    public String hostname = ""; // CraftBukkit - add field

    @Override
    public String bridge$hostname() {
        return hostname;
    }

    @Override
    public void taiyitist$setHostName(String hostName) {
        this.hostname = hostName;
    }

    // Spigot Start
    @Override
    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }
    // Spigot End

    @Redirect(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;awaitUninterruptibly()Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture taiyitist$cancelAwait(ChannelFuture instance) {
        return this.channel.close();
    }
}
