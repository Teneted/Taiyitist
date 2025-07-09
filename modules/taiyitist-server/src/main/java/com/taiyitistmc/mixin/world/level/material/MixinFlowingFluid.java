package com.taiyitistmc.mixin.world.level.material;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public class MixinFlowingFluid {

    @Inject(method = "spread", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void taiyitst$blockFromToEvent(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.block.Block source = CraftBlock.at(serverLevel, blockPos);
        BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
        serverLevel.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "spreadToSides", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void taiyitst$blockFromToEvent0(ServerLevel serverLevel, BlockPos blockPos, FluidState fluidState, BlockState blockState, CallbackInfo ci, @Local Direction direction) {
        // CraftBukkit start
        org.bukkit.block.Block source = CraftBlock.at(serverLevel, blockPos);
        BlockFromToEvent event = new BlockFromToEvent(source, org.bukkit.craftbukkit.block.CraftBlock.notchToBlockFace(direction));
        serverLevel.getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void taiyitist$FluidLevelChangeEvent(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        // CraftBukkit start
        FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(serverLevel, blockPos, blockState);
        if (event.isCancelled()) {
            ci.cancel();
        }
        blockState = ((CraftBlockData) event.getNewData()).getState();
        // CraftBukkit end
    }
}
