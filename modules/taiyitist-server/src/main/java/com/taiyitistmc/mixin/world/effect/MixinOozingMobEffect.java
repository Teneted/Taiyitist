package com.taiyitistmc.mixin.world.effect;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.OozingMobEffect")
public class MixinOozingMobEffect {

    @Inject(method = "spawnSlimeOffspring", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void taiyitist$pushSpawnReason(Level level, double d, double e, double f, CallbackInfo ci) {
        level.pushAddEntityReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.POTION_EFFECT);
    }
}
