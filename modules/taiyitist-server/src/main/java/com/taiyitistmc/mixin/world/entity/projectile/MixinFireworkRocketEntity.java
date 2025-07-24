package com.taiyitistmc.mixin.world.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity extends Projectile {

    @Shadow private int life;

    @Shadow private int lifetime;

    @Shadow protected abstract void explode(ServerLevel serverLevel);

    public MixinFireworkRocketEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "explode", cancellable = true, at = @At("HEAD"))
    private void taiyitist$fireworksExplode(CallbackInfo ci) {
        if (CraftEventFactory.callFireworkExplodeEvent((FireworkRocketEntity) (Object) this).isCancelled()) {
            ci.cancel();
        }
    }

    // Spigot Start - copied from tick
    @Override
    public void inactiveTick() {
        this.life += 1;
        if (!this.level().isClientSide && this.life > this.lifetime) {
            // CraftBukkit start
            if (!CraftEventFactory.callFireworkExplodeEvent(((FireworkRocketEntity) (Object) this)).isCancelled() && this.level() instanceof ServerLevel serverLevel) {
                this.explode(serverLevel);
            }
            // CraftBukkit end
        }
        super.inactiveTick();
    }
    // Spigot End

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;discard()V"))
    private void taiyitist$pushRemoveCause(ServerLevel serverLevel, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.EXPLODE);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection taiyitist$resetHit(FireworkRocketEntity instance, HitResult hitResult) {
        return preHitTargetOrDeflectSelf(hitResult); // CraftBukkit - projectile hit event
    }
}
