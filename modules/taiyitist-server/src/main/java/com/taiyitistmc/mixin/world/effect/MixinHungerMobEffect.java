package com.taiyitistmc.mixin.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.effect.HungerMobEffect")
public class MixinHungerMobEffect {

    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void taiyitist$pushHealReason(ServerLevel serverLevel, LivingEntity livingEntity, int i, CallbackInfoReturnable<Boolean> cir) {
        livingEntity.pushExhaustionCause(org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason.HUNGER_EFFECT);
    }
}
