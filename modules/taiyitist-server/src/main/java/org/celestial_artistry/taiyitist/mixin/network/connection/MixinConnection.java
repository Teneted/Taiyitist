package org.celestial_artistry.taiyitist.mixin.network.connection;

import org.celestial_artistry.taiyitist.injection.network.connection.InjectionConnection;
import io.netty.channel.Channel;
import java.net.SocketAddress;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Connection.class)
public class MixinConnection implements InjectionConnection {

    @Shadow public Channel channel;
    @Unique
    public String hostname = ""; // CraftBukkit - add field

    @Override
    public String bridge$hostname() {
        return hostname;
    }

    @Override
    public void taiyitist$setHostName(String hostName) {
        hostname = hostName;
    }

    // Spigot Start
    @Override
    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }
    // Spigot End
}
