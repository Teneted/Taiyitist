package com.taiyitistmc.mixin.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StopAttackingIfTargetInvalid.class)
public class MixinStopAttackingIfTargetInvalid {

    @Inject(method = "method_47135", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/StopAttackingIfTargetInvalid$TargetErasedCallback;accept(Lnet/minecraft/server/level/ServerLevel;Ljava/lang/Object;Lnet/minecraft/world/entity/LivingEntity;)V"), cancellable = true)
    private static void taiyitist$callEntityTargetLivingEvent(BehaviorBuilder.Instance instance, MemoryAccessor memoryAccessor,
                                                              boolean bl, MemoryAccessor memoryAccessor2,
                                                              StopAttackingIfTargetInvalid.StopAttackCondition stopAttackCondition,
                                                              StopAttackingIfTargetInvalid.TargetErasedCallback targetErasedCallback,
                                                              ServerLevel serverLevel, Mob mob, long l, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        LivingEntity old = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(mob, null, (old != null && !old.isAlive()) ? EntityTargetEvent.TargetReason.TARGET_DIED : EntityTargetEvent.TargetReason.FORGOT_TARGET);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        if (event.getTarget() != null) {
            mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ((CraftLivingEntity) event.getTarget()).getHandle());
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }

}
