package com.taiyitistmc.mixin.world.entity.projectile.windcharge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWindCharge.class)
public abstract class MixinAbstractWindCharge extends AbstractHurtingProjectile implements ItemSupplier {

    protected MixinAbstractWindCharge(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/windcharge/AbstractWindCharge;discard()V"))
    private void taiyitist$pushRemoveCause0(BlockHitResult blockHitResult, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/windcharge/AbstractWindCharge;discard()V"))
    private void taiyitist$pushRemoveCause1(HitResult hitResult, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/windcharge/AbstractWindCharge;discard()V"))
    private void taiyitist$pushRemoveCause2(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.OUT_OF_WORLD);
    }
}
