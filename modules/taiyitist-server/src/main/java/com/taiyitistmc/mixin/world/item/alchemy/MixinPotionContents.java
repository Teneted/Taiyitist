package com.taiyitistmc.mixin.world.item.alchemy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionContents.class)
public class MixinPotionContents {

    @Inject(method = "method_62840", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static void taiyitist$pushEffectCause(ServerLevel serverLevel, Player player, LivingEntity livingEntity, MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.POTION_DRINK);
    }
}
