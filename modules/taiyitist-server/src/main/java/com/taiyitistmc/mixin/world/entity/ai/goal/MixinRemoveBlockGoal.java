package com.taiyitistmc.mixin.world.entity.ai.goal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RemoveBlockGoal.class)
public class MixinRemoveBlockGoal {

    // @formatter:off
    @Shadow @Final private Mob removerMob;
    // @formatter:on

    @Inject(method = "tick", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public void taiyitist$removeBlock(CallbackInfo ci, @Local Level world, @Local(ordinal = 1) BlockPos pos1) {
        EntityInteractEvent event = new EntityInteractEvent(this.removerMob.getBukkitEntity(), CraftBlock.at(world, pos1));
        world.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
