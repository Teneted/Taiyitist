package com.taiyitistmc.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownExperienceBottle.class)
public abstract class MixinThrownExperienceBottle  extends ThrowableItemProjectile {

    public MixinThrownExperienceBottle(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void taiyitist$moveDown(ServerLevel instance, int i, BlockPos blockPos, int i2, @Local(argsOnly = true) HitResult hitResult) {
        // CraftBukkit start
        ExpBottleEvent event = CraftEventFactory.callExpBottleEvent(this, hitResult, 3 + instance.random.nextInt(5) + instance.random.nextInt(5));
        i = event.getExperience();
        if (event.getShowEffect()) {
            instance.levelEvent(i, this.blockPosition(), i2);
        }
        // CraftBukkit end
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownExperienceBottle;discard()V"))
    private void taiyitist$pushRemoveCause(HitResult hitResult, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }
}
