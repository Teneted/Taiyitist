package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.BlockFromToEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonEggBlock.class)
public class MixinDragonEggBlock {

    @Inject(method = "teleport", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z"), cancellable = true)
    private void taiyitist$BlockFromToEvent(BlockState blockState, Level world, BlockPos blockposition, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockposition1) {
        // CraftBukkit start
        org.bukkit.block.Block from = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        org.bukkit.block.Block to = world.getWorld().getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
        BlockFromToEvent event = new BlockFromToEvent(from, to);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }

        blockposition1 = new BlockPos(event.getToBlock().getX(), event.getToBlock().getY(), event.getToBlock().getZ());
        // CraftBukkit end
    }
}
