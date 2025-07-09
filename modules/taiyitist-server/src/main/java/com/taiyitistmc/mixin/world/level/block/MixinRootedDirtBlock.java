package com.taiyitistmc.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RootedDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RootedDirtBlock.class)
public class MixinRootedDirtBlock {

    @Redirect(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean taiyitist$handleBlockSpreadEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, blockPos, blockPos.below(), Blocks.HANGING_ROOTS.defaultBlockState()); // CraftBukkit;
    }
}
