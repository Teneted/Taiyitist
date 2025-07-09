package com.taiyitistmc.mixin.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.effect.HealOrHarmMobEffect")
public class MixinHealOrHarmMobEffect {

    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void taiyitist$pushHealReason(ServerLevel serverLevel, LivingEntity livingEntity, int i, CallbackInfoReturnable<Boolean> cir) {
        livingEntity.pushHealReason(org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.MAGIC);
    }

    @Inject(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void taiyitist$pushHealReason0(ServerLevel serverLevel, Entity entity, Entity entity2, LivingEntity livingEntity, int i, double d, CallbackInfo ci) {
        livingEntity.pushHealReason(org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.MAGIC);
    }
}
