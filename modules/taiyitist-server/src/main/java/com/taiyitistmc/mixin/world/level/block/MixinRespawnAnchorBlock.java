package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RespawnAnchorBlock.class)
public class MixinRespawnAnchorBlock {

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/server/level/ServerPlayer$RespawnConfig;Z)V"))
    private void taiyitist$pushSpawnChangeCause(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        player.pushSpawnChangeCause(PlayerSpawnChangeEvent.Cause.RESPAWN_ANCHOR);
    }

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;badRespawnPointExplosion(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource taiyitist$addBukkitState(DamageSources instance, Vec3 vec3, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos blockPos) {
        org.bukkit.block.BlockState blockState = org.bukkit.craftbukkit.block.CraftBlock.at(level, blockPos).getState(); // CraftBukkit - capture BlockState before remove block
        return level.damageSources().badRespawnPointExplosion(vec3, blockState);
    }
}
