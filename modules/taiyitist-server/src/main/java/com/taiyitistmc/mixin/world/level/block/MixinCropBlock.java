package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CropBlock.class)
public abstract class MixinCropBlock {

    @Shadow public abstract BlockState getStateForAge(int i);

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean banner$handleBlockGrowEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, this.getStateForAge(i + 1), 2); // CraftBukkit;
    }

    @Redirect(method = "growCrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean banner$handleBlockGrowEvent0(Level instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, this.getStateForAge(i), 2); // CraftBukkit;
    }

    @ModifyExpressionValue(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean banner$callEntityChangeBlockEvent(boolean original, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) Level level, @Local(argsOnly = true) Entity entity) {
        return CraftEventFactory.callEntityChangeBlockEvent(entity, blockPos, Blocks.AIR.defaultBlockState(), !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING));
    }
}
