package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EyeblossomBlock.class)
public class MixinEyeblossomBlock {

    @Redirect(method = "tryChangingState", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean taiyitist$handleBlockFormEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Local EyeblossomBlock.Type type, @Cancellable CallbackInfoReturnable<Boolean> ci) {
        // CraftBukkit start - BlockFormEvent
        boolean taiyitist$event = CraftEventFactory.handleBlockFormEvent(instance, blockPos, type.state(), 3);
        if (!taiyitist$event) {
            ci.setReturnValue(false);
        }
        return taiyitist$event;
        // CraftBukkit end
    }
}
