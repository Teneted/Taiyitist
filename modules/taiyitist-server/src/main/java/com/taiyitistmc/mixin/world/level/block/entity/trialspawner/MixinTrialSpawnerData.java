package com.taiyitistmc.mixin.world.level.block.entity.trialspawner;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawnerData.class)
public class MixinTrialSpawnerData {

    @Inject(method = "method_58713", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private static void banner$pushDespawnCause(ServerLevel serverLevel, Entity entity, CallbackInfo ci) {
        entity.pushRemoveCause(org.bukkit.event.entity.EntityRemoveEvent.Cause.DESPAWN);
    }
}
