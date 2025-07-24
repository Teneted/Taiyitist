package com.taiyitistmc.mixin.world.entity.raid;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.raid.Raider.HoldGroundAttackGoal")
public class MixinRaider_HoldGroundAttackGoal {

    @Inject(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void taiyitist$pushTargetReason(CallbackInfo ci, @Local Raider raider) {
        raider.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.FOLLOW_LEADER, true);
    }

    @Inject(method = "stop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void taiyitist$pushTargetReason0(CallbackInfo ci, @Local Raider raider) {
        raider.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.FOLLOW_LEADER, true);
    }
}
