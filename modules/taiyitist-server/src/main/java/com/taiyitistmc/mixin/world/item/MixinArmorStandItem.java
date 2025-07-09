package com.taiyitistmc.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandItem.class)
public class MixinArmorStandItem {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"), cancellable = true)
    private void taiyitist$entityPlaceEvent(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, @Local ArmorStand armorStand) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPlaceEvent(useOnContext, armorStand).isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
        // CraftBukkit end
    }
}
