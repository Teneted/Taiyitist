package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.taiyitistmc.asm.annotation.TransformAccess;
import com.taiyitistmc.injection.world.level.block.InjectionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock extends BlockBehaviour implements InjectionBlock {

    public MixinBlock(Properties properties) {
        super(properties);
    }

    @WrapWithCondition(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean taiyitist$addCapture(Level instance, Entity entity) {
        boolean taiyitist$flag = instance.bridge$captureDrops() != null;
        if (taiyitist$flag) {
            instance.bridge$captureDrops().add((ItemEntity) entity);
        }
        return !taiyitist$flag;
    }

    // Spigot start
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static float range(float min, float value, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Override
    public int getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean flag) {
        return 0;
    }

    @Override
    public int taiyitist$tryDropExperience(ServerLevel level, BlockPos pos, ItemStack heldItem, IntProvider amount) {
        return tryDropExperience(level, pos, heldItem, amount);
    }

    protected int tryDropExperience(ServerLevel worldserver, BlockPos blockposition, ItemStack itemstack, IntProvider intprovider) {
        int i = EnchantmentHelper.processBlockExperience(worldserver, itemstack, intprovider.sample(worldserver.getRandom()));
        if (i > 0) {
            return i;
        }
        return 0;
    }

    @Inject(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void taiyitist$reason(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        player.pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.BLOCK_MINED);
    }
    // Spigot end
}
