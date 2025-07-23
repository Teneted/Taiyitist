package com.taiyitistmc.mixin.world.item.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ApplyMobEffect.class)
public class MixinApplyMobEffect {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void taiyitst$pushEffectCause(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
        }
    }
}
