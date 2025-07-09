package com.taiyitistmc.mixin.world.level.material;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class MixinLavaFluid {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0), cancellable = true)
    private void taiyitist$callBlockIgniteEvent(ServerLevel serverLevel, BlockPos blockPos,
                                                FluidState fluidState, RandomSource randomSource,
                                                CallbackInfo ci, @Local(ordinal = 1) BlockPos blockPos2) {
        // CraftBukkit start - Prevent lava putting something on fire
        if (serverLevel.getBlockState(blockPos2).getBlock() != Blocks.FIRE) {
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(serverLevel, blockPos2, blockPos).isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1), cancellable = true)
    private void taiyitist$callBlockIgniteEvent0(ServerLevel serverLevel, BlockPos blockPos,
                                                FluidState fluidState, RandomSource randomSource,
                                                CallbackInfo ci, @Local(ordinal = 1) BlockPos blockPos2) {
        // CraftBukkit start - Prevent lava putting something on fire
        BlockPos up = blockPos2.above();
        if (serverLevel.getBlockState(up).getBlock() != Blocks.FIRE) {
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(serverLevel, up, blockPos).isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @Redirect(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean taiyitist$handleBlockFormEvent(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int i) {
        // CraftBukkit start
        return org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(instance.getMinecraftWorld(), blockPos, Blocks.STONE.defaultBlockState(), 3);
        // CraftBukkit end
    }
}
