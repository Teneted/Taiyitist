package com.taiyitistmc.mixin.world.entity;

import com.taiyitistmc.injection.world.entity.InjectionNeutralMob;
import io.izzel.arclight.mixin.Decorate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NeutralMob.class)
public interface MixinNeutralMob extends InjectionNeutralMob {

    @Shadow
    void setTarget(@Nullable LivingEntity livingEntity);

    @Inject(method = "readPersistentAngerSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void taiyitist$targetReason(Level level, ValueInput valueInput, CallbackInfo ci) {
        if (this instanceof Mob b) {
            b.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.UNKNOWN, false);
        }
    }

    @Inject(method = "stopBeingAngry", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void taiyitist$targetReason0(CallbackInfo ci) {
        if (this instanceof Mob b) {
            b.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.FORGOT_TARGET, false);
        }
    }

    @Override
    boolean setTarget(@Nullable LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent);

}
