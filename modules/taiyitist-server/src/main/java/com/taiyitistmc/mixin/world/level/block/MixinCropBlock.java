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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class MixinCropBlock {

    @Redirect(method = "growCrops(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean taiyitist$blockGrowGrow(Level world, BlockPos pos, BlockState newState, int flags) {
        return CraftEventFactory.handleBlockGrowEvent(world, pos, newState, flags);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public boolean taiyitist$blockGrowTick(ServerLevel world, BlockPos pos, BlockState newState, int flags) {
        return CraftEventFactory.handleBlockGrowEvent(world, pos, newState, flags);
    }

    @ModifyExpressionValue(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean taiyitist$callEntityChangeBlockEvent(boolean original, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) Level level, @Local(argsOnly = true) Entity entity) {
        return CraftEventFactory.callEntityChangeBlockEvent(entity, blockPos, Blocks.AIR.defaultBlockState(), !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING));
    }
}
