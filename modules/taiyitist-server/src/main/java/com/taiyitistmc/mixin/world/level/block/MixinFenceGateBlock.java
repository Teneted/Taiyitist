package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FenceGateBlock.class)
public class MixinFenceGateBlock {

    @Inject(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private void taiyitist$callRedStoneEvent(BlockState blockState, Level level, BlockPos blockPos, Block block, Orientation orientation, boolean bl, CallbackInfo ci, @Local(ordinal = 1) boolean bl2) {
        // CraftBukkit start
        boolean oldPowered = blockState.getValue(FenceGateBlock.POWERED);
        if (oldPowered != bl2) {
            int newPower = bl2 ? 15 : 0;
            int oldPower = oldPowered ? 15 : 0;
            org.bukkit.block.Block bukkitBlock = org.bukkit.craftbukkit.block.CraftBlock.at(level, blockPos);
            org.bukkit.event.block.BlockRedstoneEvent eventRedstone = new org.bukkit.event.block.BlockRedstoneEvent(bukkitBlock, oldPower, newPower);
            level.getCraftServer().getPluginManager().callEvent(eventRedstone);
            bl2 = eventRedstone.getNewCurrent() > 0;
        }
        // CraftBukkit end
    }
}
