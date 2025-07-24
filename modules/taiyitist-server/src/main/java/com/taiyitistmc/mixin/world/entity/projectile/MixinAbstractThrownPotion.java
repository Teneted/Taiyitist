package com.taiyitistmc.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractThrownPotion.class)
public abstract class MixinAbstractThrownPotion extends ThrowableItemProjectile {

    public MixinAbstractThrownPotion(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractThrownPotion;discard()V"))
    private void taiyitist$pushDiscardCause(HitResult hitResult, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @WrapWithCondition(method = "dowseFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$dowseFire0(Level instance, BlockPos pos, boolean b, Entity entity) {
        return CraftEventFactory.callEntityChangeBlockEvent(this, pos, Blocks.AIR.defaultBlockState());
    }

    @WrapWithCondition(method = "dowseFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AbstractCandleBlock;extinguish(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)V"))
    private boolean taiyitist$dowseFire1(Player player, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        return CraftEventFactory.callEntityChangeBlockEvent(this, blockPos, blockState.setValue(AbstractCandleBlock.LIT, false));
    }

    @ModifyExpressionValue(method = "dowseFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CampfireBlock;isLitCampfire(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean taiyitist$dowseFire2(boolean original, @Local(argsOnly = true) BlockPos blockPos, @Local BlockState blockState) {
        return original && CraftEventFactory.callEntityChangeBlockEvent(this, blockPos, blockState.setValue(CampfireBlock.LIT, false));
    }
}
