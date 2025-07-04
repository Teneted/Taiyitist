package com.mohistmc.banner.mixin.world.level;

import com.mohistmc.banner.injection.world.level.InjectionExplosion;
import com.mojang.datafixers.util.Pair;
import io.izzel.arclight.mixin.Decorate;
import io.izzel.arclight.mixin.DecorationOps;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class MixinExplosion implements InjectionExplosion {

    @Shadow @Final public Entity source;
    // @formatter:off
    @Shadow @Final private Level level;
    @Shadow @Final private Explosion.BlockInteraction blockInteraction;
    @Shadow @Final private ExplosionDamageCalculator damageCalculator;
    @Shadow @Mutable @Final private float radius;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    // @formatter:on

    @Unique public boolean wasCanceled = false; // CraftBukkit - add field
    @Unique public float yield;

    @Inject(method = "addOrAppendStack", cancellable = true, at = @At("HEAD"))
    private static void banner$fix(List<Pair<ItemStack, BlockPos>> p_311090_, ItemStack stack, BlockPos p_309821_, CallbackInfo ci) {
        if (stack.isEmpty()) ci.cancel();
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)V",
            at = @At("RETURN"))
    public void banner$adjustSize(Level worldIn, Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Explosion.BlockInteraction modeIn, CallbackInfo ci) {
        this.radius = Math.max(sizeIn, 0F);
        this.yield = this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY ? 1.0F / this.radius : 1.0F;
    }

    @Override
    public float bridge$getYield() {
        return yield;
    }

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    public void banner$explode(CallbackInfo ci) {
        // CraftBukkit start
        if (this.radius < 0.1F) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Decorate(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean banner$handleMultiPart(Entity entity, DamageSource damageSource, float f) throws Throwable {
        // Special case ender dragon only give knockback if no damage is cancelled
        // Thinks to note:
        // - Setting a velocity to a ComplexEntityPart is ignored (and therefore not needed)
        // - Damaging ComplexEntityPart while forward the damage to EntityEnderDragon
        // - Damaging EntityEnderDragon does nothing
        // - EntityEnderDragon hitbock always covers the other parts and is therefore always present
        entity.banner$setLastDamageCancelled(false);

        if (entity.bridge$lastDamageCancelled()) {
            throw DecorationOps.jumpToLoopStart();
        }
        return (boolean) DecorationOps.callsite().invoke(entity, damageSource, f);
    }

    @Redirect(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    private void banner$setDeltaMovement(Entity instance, Vec3 vec3) {
        float q = this.radius * 2.0F;
        double v = Math.sqrt(instance.distanceToSqr(vec3)) / (double)q;
        double aa = ((double)1.0F - v) * (double)Explosion.getSeenPercent(vec3, instance) * (double)this.damageCalculator.getKnockbackMultiplier(instance);
        double ab;
        if (instance instanceof LivingEntity livingEntity) {
            ab = aa * ((double)1.0F - livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
        } else {
            ab = aa;
        }
        double dx = instance.getX() - this.x;
        double dy = instance.getEyeY() - this.y;
        double dz = instance.getZ() - this.z;
        dx *= ab;
        dy *= ab;
        dz *= ab;
        Vec3 vec31 = new Vec3(dx, dy, dz);
        Vec3 result = instance.getDeltaMovement().add(vec31);
        if (instance instanceof LivingEntity livingEntity) {
            EntityKnockbackEvent event = CraftEventFactory.callEntityKnockbackEvent(
                    (CraftLivingEntity) livingEntity.getBukkitEntity(),
                    ((Explosion) (Object) this).getDirectSourceEntity(),
                    EntityKnockbackEvent.KnockbackCause.EXPLOSION,
                    ab,
                    vec31,
                    result.x,
                    result.y,
                    result.z
            );
            vec31 = (event.isCancelled()) ? Vec3.ZERO : new Vec3(event.getFinalKnockback().getX(), event.getFinalKnockback().getY(), event.getFinalKnockback().getZ());
        }
        instance.setDeltaMovement(vec31);
    }

    @Decorate(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onExplosionHit(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V"))
    private void banner$tntPrime(BlockState instance, Level level, BlockPos pos, Explosion explosion, BiConsumer<?, ?> biConsumer) throws Throwable {
        if (instance.getBlock() instanceof TntBlock) {
            var sourceEntity = source == null ? null : source;
            var sourceBlock = sourceEntity == null ? BlockPos.containing(this.x, this.y, this.z) : null;
            if (!CraftEventFactory.callTNTPrimeEvent(this.level, pos, TNTPrimeEvent.PrimeCause.EXPLOSION, sourceEntity, sourceBlock)) {
                this.level.sendBlockUpdated(pos, Blocks.AIR.defaultBlockState(), instance, 3); // Update the block on the client
                return;
            }
        }
        DecorationOps.callsite().invoke(instance, level, pos, explosion, biConsumer);
    }

    @Decorate(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean banner$blockIgnite(Level instance, BlockPos blockPos, BlockState blockState) throws Throwable {
        BlockIgniteEvent event = CraftEventFactory.callBlockIgniteEvent(this.level, blockPos, (Explosion) (Object) this);
        if (event.isCancelled()) {
            return false;
        }
        return (boolean) DecorationOps.callsite().invoke(instance, blockPos, blockState);
    }

    @Override
    public boolean bridge$wasCanceled() {
        return wasCanceled;
    }

    @Override
    public void banner$setWasCanceled(boolean wasCanceled) {
        this.wasCanceled = wasCanceled;
    }
}
