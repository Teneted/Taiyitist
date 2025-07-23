package com.taiyitistmc.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.TryLaySpawnOnWaterNearLand;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TryLaySpawnOnWaterNearLand.class)
public class MixinTryLaySpawnOnWaterNearLand {

    @Inject(method = "method_47182", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private static void taiyitist$callEntityChangeBlockEvent(Block block, MemoryAccessor memoryAccessor, ServerLevel serverLevel, LivingEntity livingEntity, long l, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos blockPos, @Local BlockState blockState) {
        // CraftBukkit start
        if (!CraftEventFactory.callEntityChangeBlockEvent(livingEntity, blockPos, blockState)) {
            memoryAccessor.erase();
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
