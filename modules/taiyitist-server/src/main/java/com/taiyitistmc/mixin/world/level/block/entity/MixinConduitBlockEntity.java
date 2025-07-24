package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Objects;

@Mixin(ConduitBlockEntity.class)
public abstract class MixinConduitBlockEntity {

    @Shadow
    @Nullable
    public static EntityReference<LivingEntity> updateDestroyTarget(@Nullable EntityReference<LivingEntity> entityReference, ServerLevel serverLevel, BlockPos blockPos, boolean bl) {
        return null;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static int getRange(List<BlockPos> list) {
        // CraftBukkit end
        int i = list.size();
        int j = i / 7 * 16;
        // CraftBukkit start
        return j;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static void updateAndAttackTarget(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, ConduitBlockEntity conduitBlockEntity, boolean bl, boolean damageTarget) {
        EntityReference<LivingEntity> entityReference = updateDestroyTarget(conduitBlockEntity.destroyTarget, serverLevel, blockPos, bl);
        LivingEntity livingEntity = (LivingEntity) EntityReference.get(entityReference, serverLevel, LivingEntity.class);
        // CraftBukkit start
        if (damageTarget && livingEntity != null) {
            if (livingEntity.hurtServer(serverLevel, serverLevel.damageSources().magic().directBlock(serverLevel, blockPos), 4.0F)) {
                serverLevel.playSound((Entity)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            // CraftBukkit end
        }

        if (!Objects.equals(entityReference, conduitBlockEntity.destroyTarget)) {
            conduitBlockEntity.destroyTarget = entityReference;
            serverLevel.sendBlockUpdated(blockPos, blockState, blockState, 2);
        }

    }
}
