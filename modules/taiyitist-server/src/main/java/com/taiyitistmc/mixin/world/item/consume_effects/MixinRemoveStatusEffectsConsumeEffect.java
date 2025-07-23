package com.taiyitistmc.mixin.world.item.consume_effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemoveStatusEffectsConsumeEffect.class)
public class MixinRemoveStatusEffectsConsumeEffect {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private void taiyitst$pushEffectCause(Level level, ItemStack itemStack, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.UNKNOWN);
    }
}
