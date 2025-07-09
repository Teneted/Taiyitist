package com.taiyitistmc.mixin.util;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SpawnUtil.class)
public abstract class MixinSpawnUtil {

    @Shadow
    private static boolean moveToPossibleSpawnPosition(ServerLevel serverLevel, int i, BlockPos.MutableBlockPos mutableBlockPos, SpawnUtil.Strategy strategy) {
        return false;
    }

    @Inject(method = "trySpawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private static <T extends Mob> void taiyitist$pushSpawnReason(EntityType<T> entityType, EntitySpawnReason entitySpawnReason, ServerLevel serverLevel, BlockPos blockPos, int i, int j, int k, SpawnUtil.Strategy strategy, boolean bl, CallbackInfoReturnable<Optional<T>> cir) {
        serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Inject(method = "trySpawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;discard()V"))
    private static <T extends Mob> void taiyitist$pushRemoveReason(EntityType<T> entityType, EntitySpawnReason entitySpawnReason, ServerLevel serverLevel, BlockPos blockPos, int i, int j, int k, SpawnUtil.Strategy strategy, boolean bl, CallbackInfoReturnable<Optional<T>> cir, @Local T mob) {
        mob.pushRemoveCause(null);
    }

    @Inject(method = "trySpawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;playAmbientSound()V"), cancellable = true)
    private static <T extends Mob> void taiyitist$checkRemoved(EntityType<T> entityType, EntitySpawnReason entitySpawnReason, ServerLevel serverLevel, BlockPos blockPos, int i, int j, int k, SpawnUtil.Strategy strategy, boolean bl, CallbackInfoReturnable<Optional<T>> cir, @Local T mob) {
        if (mob.isRemoved()) cir.setReturnValue(Optional.empty()); // CraftBukkit
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static <T extends Mob> Optional<T> trySpawnMob(EntityType<T> entityType, EntitySpawnReason entitySpawnReason, ServerLevel serverLevel, BlockPos blockPos, int i, int j, int k, SpawnUtil.Strategy strategy, boolean bl, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();

        for(int l = 0; l < i; ++l) {
            int m = Mth.randomBetweenInclusive(serverLevel.random, -j, j);
            int n = Mth.randomBetweenInclusive(serverLevel.random, -j, j);
            mutableBlockPos.setWithOffset(blockPos, m, k, n);
            if (serverLevel.getWorldBorder().isWithinBounds(mutableBlockPos) && moveToPossibleSpawnPosition(serverLevel, k, mutableBlockPos, strategy) && (!bl || serverLevel.noCollision(entityType.getSpawnAABB((double)mutableBlockPos.getX() + 0.5, (double)mutableBlockPos.getY(), (double)mutableBlockPos.getZ() + 0.5)))) {
                T mob = (T) entityType.create(serverLevel, null, mutableBlockPos, entitySpawnReason, false, false);
                if (mob != null) {
                    if (mob.checkSpawnRules(serverLevel, entitySpawnReason) && mob.checkSpawnObstruction(serverLevel)) {
                        serverLevel.addFreshEntityWithPassengers(mob, reason);
                        if (mob.isRemoved()) return Optional.empty(); // CraftBukkit
                        mob.playAmbientSound();
                        return Optional.of(mob);
                    }

                    mob.discard(null);
                }
            }
        }

        return Optional.empty();
    }
}
