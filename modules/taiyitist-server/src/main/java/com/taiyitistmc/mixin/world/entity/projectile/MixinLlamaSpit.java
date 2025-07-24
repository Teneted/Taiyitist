package com.taiyitistmc.mixin.world.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LlamaSpit.class)
public abstract class MixinLlamaSpit extends Projectile {

    public MixinLlamaSpit(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection taiyitist$resetHit(LlamaSpit instance, HitResult hitResult) {
        return this.preHitTargetOrDeflectSelf(hitResult);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;discard()V"))
    private void taiyitist$pushRemoveCause(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Inject(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;discard()V"))
    private void taiyitist$pushHitCause(BlockHitResult blockHitResult, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }
}
