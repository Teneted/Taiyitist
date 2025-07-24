package com.taiyitistmc.mixin.server.level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.DistanceManager.FixedPlayerDistanceChunkTracker")
public class MixinFixedPlayerDistanceChunkTracker {

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void taiyitist$catchOp(long l, int i, CallbackInfo ci) {
        org.spigotmc.AsyncCatcher.catchOp("chunk level update"); // Spigot
    }
}
