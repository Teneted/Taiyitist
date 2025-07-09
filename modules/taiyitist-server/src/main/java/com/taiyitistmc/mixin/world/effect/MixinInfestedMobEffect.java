package com.taiyitistmc.mixin.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.InfestedMobEffect")
public class MixinInfestedMobEffect {

    @Inject(method = "spawnSilverfish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void taiyitist$pushSpawnCause(ServerLevel serverLevel, LivingEntity livingEntity, double d, double e, double f, CallbackInfo ci) {
        livingEntity.pushSpawnCause(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.POTION_EFFECT);
    }
}
