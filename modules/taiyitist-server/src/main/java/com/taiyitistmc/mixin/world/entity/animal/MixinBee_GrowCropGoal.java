package com.taiyitistmc.mixin.world.entity.animal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.world.entity.animal.Bee$BeeGrowCropGoal")
public class MixinBee_GrowCropGoal {

    @SuppressWarnings("target")
    @Shadow(aliases = {"field_20373"}, remap = false)
    private Bee outerThis;

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void taiyitist$entityChangeBlock(CallbackInfo ci, @Local BlockPos blockPos, @Local(ordinal = 1) BlockState blockState2) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(outerThis, blockPos, blockState2)) {
            ci.cancel();
        }
    }
}
