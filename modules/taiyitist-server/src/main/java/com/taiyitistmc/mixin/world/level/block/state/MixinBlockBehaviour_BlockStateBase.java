package com.taiyitistmc.mixin.world.level.block.state;

import com.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockBehaviour_BlockStateBase {

    @Inject(method = "entityInside", at = @At("HEAD"))
    private void taiyitist$captureBlockCollide(Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, CallbackInfo ci) {
        BukkitSnapshotCaptures.captureDamageEventBlock(blockPos);
    }

    @Inject(method = "entityInside", at = @At("RETURN"))
    private void taiyitist$resetBlockCollide(Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, CallbackInfo ci) {
        BukkitSnapshotCaptures.captureDamageEventBlock(null);
    }
}
