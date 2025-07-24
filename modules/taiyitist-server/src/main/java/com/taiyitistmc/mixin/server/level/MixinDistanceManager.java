package com.taiyitistmc.mixin.server.level;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DistanceManager.class)
public class MixinDistanceManager {

    @Inject(method = "runAllUpdates", at = @At("HEAD"))
    private void taiyitistcatchOp(ChunkMap chunkMap, CallbackInfoReturnable<Boolean> cir) {
        org.spigotmc.AsyncCatcher.catchOp("chunk updates"); // Spigot
    }
}
