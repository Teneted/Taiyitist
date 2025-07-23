package com.taiyitistmc.mixin.world.entity.ai.goal;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EatBlockGoal.class)
public class MixinEatBlockGoal {

    @Shadow @Final private Mob mob;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z", ordinal = 0))
    private boolean taiyitist$callEntityChangeBlockEvent0(boolean original, @Local BlockPos blockPos) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockPos, Blocks.AIR.defaultBlockState(), !original);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z", ordinal = 1))
    private boolean taiyitist$callEntityChangeBlockEvent1(boolean original, @Local(ordinal = 1) BlockPos blockPos) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockPos, Blocks.AIR.defaultBlockState(), !original);
    }
}
