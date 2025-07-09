package com.taiyitistmc.mixin.world.item;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BoneMealItem.class)
public abstract class MixinBoneMealItem {

    @Shadow
    public static boolean growCrop(ItemStack itemStack, Level level, BlockPos blockPos) {
        return false;
    }

    @Shadow
    public static boolean growWaterPlant(ItemStack itemStack, Level level, BlockPos blockPos, @Nullable Direction direction) {
        return false;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static InteractionResult applyBonemeal(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(useOnContext.getClickedFace());
        if (growCrop(useOnContext.getItemInHand(), level, blockPos)) {
            if (!level.isClientSide) {
                if (useOnContext.getPlayer() != null) useOnContext.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                level.levelEvent(1505, blockPos, 15);
            }

            return InteractionResult.SUCCESS;
        } else {
            BlockState blockState = level.getBlockState(blockPos);
            boolean bl = blockState.isFaceSturdy(level, blockPos, useOnContext.getClickedFace());
            if (bl && growWaterPlant(useOnContext.getItemInHand(), level, blockPos2, useOnContext.getClickedFace())) {
                if (!level.isClientSide) {
                    if (useOnContext.getPlayer() != null) useOnContext.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                    level.levelEvent(1505, blockPos2, 15);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
