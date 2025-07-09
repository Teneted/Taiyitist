package com.taiyitistmc.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirtPathBlock.class)
public class MixinDirtPathBlock {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void taiyitist$checkCanSurvive(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        // CraftBukkit start - do not fade if the block is valid here
        if (blockState.canSurvive(serverLevel, blockPos)) {
            ci.cancel();
        }
        // CraftBukkit end
    }
}
