package com.taiyitistmc.mixin.world.entity.monster;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal")
public class MixinEnderMan_EndermanLeaveBlockGoal {

    // @formatter:off
    @Shadow @Final private EnderMan enderman;
    // @formatter:on

    @Inject(method = "tick", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void taiyitist$entityChangeBlock(CallbackInfo ci, @Local(ordinal = 0) BlockPos blockPos, @Local(ordinal = 2) BlockState blockState2) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(this.enderman, blockPos, blockState2)) {
            ci.cancel();
        }
    }
}
