package com.taiyitistmc.mixin.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ThrownEnderpearl.class)
public abstract class MixinThrownEnderpearl extends ThrowableItemProjectile {

    @Shadow
    private static boolean isAllowedToTeleportOwner(Entity entity, Level level) {
        return false;
    }

    @Shadow protected abstract void playSound(Level level, Vec3 vec3);

    public MixinThrownEnderpearl(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        for(int i = 0; i < 32; ++i) {
            this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }

        Level var3 = this.level();
        if (var3 instanceof ServerLevel serverLevel) {
            if (!this.isRemoved()) {
                Entity entity = this.getOwner();
                if (entity != null && isAllowedToTeleportOwner(entity, serverLevel)) {
                    Vec3 vec3 = this.oldPosition();
                    if (entity instanceof ServerPlayer serverPlayer) {
                        if (serverPlayer.connection.isAcceptingMessages()) {
                            // CraftBukkit start
                            serverPlayer.pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                            ServerPlayer serverPlayer2 = serverPlayer.teleport(new TeleportTransition(serverLevel, vec3, Vec3.ZERO, 0.0F, 0.0F, Relative.union(new Set[]{Relative.ROTATION, Relative.DELTA}), TeleportTransition.DO_NOTHING));
                            if (serverPlayer2 == null) {
                                this.discard(EntityRemoveEvent.Cause.HIT);
                                return;
                            }
                            // CraftBukkit end
                            if (this.random.nextFloat() < 0.05F && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                                Endermite endermite = (Endermite)EntityType.ENDERMITE.create(serverLevel, EntitySpawnReason.TRIGGERED);
                                if (endermite != null) {
                                    endermite.snapTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                                    serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.ENDER_PEARL);
                                    serverLevel.addFreshEntity(endermite);
                                }
                            }

                            if (this.isOnPortalCooldown()) {
                                entity.setPortalCooldown();
                            }

                            if (serverPlayer2 != null) {
                                serverPlayer2.resetFallDistance();
                                serverPlayer2.resetCurrentImpulseContext();
                                serverPlayer2.hurtServer(serverPlayer.level(), this.damageSources().enderPearl().customEntityDamager(this), 5.0F); // CraftBukkit;
                            }

                            this.playSound(serverLevel, vec3);
                        }
                    } else {
                        Entity entity2 = entity.teleport(new TeleportTransition(serverLevel, vec3, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING));
                        if (entity2 != null) {
                            entity2.resetFallDistance();
                        }

                        this.playSound(serverLevel, vec3);
                    }

                    this.pushRemoveCause(EntityRemoveEvent.Cause.HIT); // CraftBukkit - add Bukkit remove cause
                    this.discard();
                    return;
                }

                this.pushRemoveCause(EntityRemoveEvent.Cause.HIT); // CraftBukkit - add Bukkit remove cause
                this.discard();
                return;
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownEnderpearl;discard()V"))
    private void taiyitist$pushRemoveCause(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN); // CraftBukkit - add Bukkit remove cause
    }
}
