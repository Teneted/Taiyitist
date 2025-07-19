package com.taiyitistmc.mixin.world.material;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public class MixinFlowingFluid {

    @Inject(method = "spread", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void taiyitst$blockFromToEvent(Level level, BlockPos pos, FluidState state, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.block.Block source = CraftBlock.at(level, pos);
        BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "spreadToSides", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void taiyitst$blockFromToEvent0(Level level, BlockPos pos, FluidState fluidState, BlockState blockState, CallbackInfo ci, @Local Direction direction) {
        // CraftBukkit start
        org.bukkit.block.Block source = CraftBlock.at(level, pos);
        BlockFromToEvent event = new BlockFromToEvent(source, CraftBlock.notchToBlockFace(direction));
        level.getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), index = 1)
    private BlockState taiyitist$FluidLevelChangeEvent(BlockState newState, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(level, pos, Blocks.AIR.defaultBlockState());
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
        return ((CraftBlockData) event.getNewData()).getState();
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), index = 1)
    private BlockState taiyitist$FluidLevelChangeEvent0(BlockState newState, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Cancellable CallbackInfo ci, @Local BlockState blockState) {
        // CraftBukkit start
        FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(level, pos, blockState);
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
        return ((CraftBlockData) event.getNewData()).getState();
    }
}
