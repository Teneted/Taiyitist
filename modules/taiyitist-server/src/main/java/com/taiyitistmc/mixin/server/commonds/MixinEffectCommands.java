package com.taiyitistmc.mixin.server.commonds;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(EffectCommands.class)
public class MixinEffectCommands {

    @Inject(method = "giveEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private static void taiyitist$pushEffectCause(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Holder<MobEffect> holder, Integer integer, int i, boolean bl, CallbackInfoReturnable<Integer> cir, @Local Entity entity) {
        entity.pushEffectCause(EntityPotionEffectEvent.Cause.COMMAND);
    }

    @Inject(method = "clearEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private static void taiyitist$pushEffectCause0(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, CallbackInfoReturnable<Integer> cir, @Local Entity entity) {
        entity.pushEffectCause(EntityPotionEffectEvent.Cause.COMMAND);
    }

    @Inject(method = "clearEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private static void taiyitist$pushEffectCause0(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Holder<MobEffect> holder, CallbackInfoReturnable<Integer> cir, @Local Entity entity) {
        entity.pushEffectCause(EntityPotionEffectEvent.Cause.COMMAND);
    }
}
