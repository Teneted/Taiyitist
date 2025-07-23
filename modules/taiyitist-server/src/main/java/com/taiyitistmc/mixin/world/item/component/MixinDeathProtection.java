package com.taiyitistmc.mixin.world.item.component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathProtection.class)
public class MixinDeathProtection {

    @Inject(method = "applyEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/consume_effects/ConsumeEffect;apply(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"))
    private void taiyitist$pushEffectCause(ItemStack itemStack, LivingEntity livingEntity, CallbackInfo ci) {
        livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.TOTEM);
    }
}
