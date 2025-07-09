package com.taiyitistmc.mixin.server.rcon.thread;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.RconClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Socket;

@Mixin(RconClient.class)
public class MixinRconClient {

    @Mutable
    @Shadow @Final private ServerInterface serverInterface;
    private RconConsoleSource rconConsoleSource;
    // CraftBukkit end

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$reset(ServerInterface serverInterface, String string, Socket socket, CallbackInfo ci) {
        this.serverInterface =  (DedicatedServer) serverInterface; // CraftBukkit
        this.rconConsoleSource = new net.minecraft.server.rcon.RconConsoleSource((MinecraftServer) this.serverInterface); // CraftBukkit
        this.rconConsoleSource.taiyitist$setSocketAddress(socket.getRemoteSocketAddress());
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/rcon/thread/RconClient;sendCmdResponse(ILjava/lang/String;)V", ordinal = 0))
    private void taiyitist$checkHeart(CallbackInfo ci) {
        ((DedicatedServer) serverInterface).taiyitist$setRconConsoleSource(this.rconConsoleSource);
    }
}
