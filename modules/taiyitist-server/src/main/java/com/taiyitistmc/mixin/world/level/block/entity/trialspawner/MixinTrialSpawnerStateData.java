package com.taiyitistmc.mixin.world.level.block.entity.trialspawner;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawnerStateData.class)
public class MixinTrialSpawnerStateData {

    @Inject(method = "method_58713", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private static void taiyitist$pushRemoveCause(ServerLevel serverLevel, Entity entity, CallbackInfo ci) {
        entity.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }
}
