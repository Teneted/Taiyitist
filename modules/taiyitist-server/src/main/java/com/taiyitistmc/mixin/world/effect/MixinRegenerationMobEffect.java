package com.taiyitistmc.mixin.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.effect.RegenerationMobEffect")
public class MixinRegenerationMobEffect {

    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void taiyitist$pushHealReason(ServerLevel serverLevel, LivingEntity livingEntity, int i, CallbackInfoReturnable<Boolean> cir) {
        livingEntity.pushHealReason(org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.MAGIC_REGEN);
    }
}
