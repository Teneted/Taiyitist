package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkShriekerBlock.class)
public abstract class MixinSculkShriekerBlock extends BaseEntityBlock {

    protected MixinSculkShriekerBlock(Properties properties) {
        super(properties);
    }

    @Inject(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Ljava/util/Optional;"), cancellable = true)
    private void taiyitist$callPlayerInteractEvent(Level level, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo ci, @Local ServerPlayer serverPlayer) {
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent(serverPlayer, org.bukkit.event.block.Action.PHYSICAL, blockPos, null, null, null).isCancelled()) ci.cancel(); // CraftBukkit
    }

    @Override
    public int getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean flag) {
        if (flag) {
            return this.taiyitist$tryDropExperience(world, blockPos, itemStack, ConstantInt.of(5));
        }
        return 0;
    }
}
