package com.taiyitistmc.paper.mixin.core.server;

import com.taiyitistmc.bukkit.BukkitFieldHooks;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow private int tickCount;
    private static final int TPS = 20;
    private static final int TICK_TIME = 1000000000 / TPS;
    private long lastTick = 0;
    private long catchupTime = 0;

    @Inject(method = "tickServer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;tickCount:I", ordinal = 1))
    private void taiyitist$callServerTickStartEvent(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        new com.destroystokyo.paper.event.server.ServerTickStartEvent(this.tickCount+1).callEvent(); // Paper - Server Tick Events
    }

    @Inject(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"))
    private void taiyitist$callServerTickEndEvent(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        // Paper start - Server Tick Events
        long endTime = System.nanoTime();
        long remaining = (TICK_TIME - (endTime - lastTick)) - catchupTime;
        new com.destroystokyo.paper.event.server.ServerTickEndEvent(this.tickCount, ((double)(endTime - lastTick) / 1000000D), remaining).callEvent();
        // Paper end - Server Tick Events
    }
}
