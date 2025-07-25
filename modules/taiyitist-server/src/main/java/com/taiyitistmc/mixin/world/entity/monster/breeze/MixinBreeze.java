package com.taiyitistmc.mixin.world.entity.monster.breeze;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Breeze.class)
public abstract class MixinBreeze {

    @Shadow @Nullable public abstract LivingEntity getTarget();

    @Inject(method = "canAttackType", at = @At("HEAD"), cancellable = true)
    private void taiyitist$allowAttack(EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (this.getTarget() != null) cir.setReturnValue(this.getTarget().getType() == entityType); // SPIGOT-7957: Allow attack if target from brain was set
    }
}
