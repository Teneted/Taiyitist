package com.taiyitistmc.mixin.world.entity.monster.creaking;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creaking.class)
public abstract class MixinCreaking extends Monster {

    protected MixinCreaking(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tearDown", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/creaking/Creaking;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$pushRemoveCause(CallbackInfo ci) {
        this.pushRemoveCause(null);
    }

    @ModifyReturnValue(method = "getHurtSound", at = @At("RETURN"))
    private SoundEvent taiyitist$returnDefaultSound(SoundEvent original) {
        return SoundEvents.CREAKING_SWAY;
    }
}
