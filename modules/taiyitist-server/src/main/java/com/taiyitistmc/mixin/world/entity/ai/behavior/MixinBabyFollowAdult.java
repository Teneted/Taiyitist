package com.taiyitistmc.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(BabyFollowAdult.class)
public class MixinBabyFollowAdult {

    @Inject(method = "method_46900", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/ai/behavior/PositionTracker;FI)Lnet/minecraft/world/entity/ai/memory/WalkTarget;"), cancellable = true)
    private static void taiyitist$callEntityTargetLivingEntityEvent(BehaviorBuilder.Instance instance, MemoryAccessor memoryAccessor, UniformInt uniformInt, boolean bl, Function function, MemoryAccessor memoryAccessor2, MemoryAccessor memoryAccessor3, ServerLevel serverLevel, LivingEntity livingEntity, long l, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) LivingEntity livingEntity1) {
        // CraftBukkit start
        EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(livingEntity, livingEntity1, EntityTargetEvent.TargetReason.FOLLOW_LEADER);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        if (event.getTarget() == null) {
            memoryAccessor.erase();
            cir.setReturnValue(true);
        }
        livingEntity1 = ((CraftLivingEntity) event.getTarget()).getHandle();
        // CraftBukkit end
    }
}
