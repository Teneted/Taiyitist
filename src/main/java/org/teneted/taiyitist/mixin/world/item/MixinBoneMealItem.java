package org.teneted.taiyitist.mixin.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.taiyitist.asm.annotation.TransformAccess;

@Mixin(BoneMealItem.class)
public abstract class MixinBoneMealItem {

    @Shadow
    public static boolean growWaterPlant(ItemStack itemStack, Level level, BlockPos pos, @Nullable Direction clickedFace) {
        return false;
    }

    @Shadow
    public static boolean growCrop(ItemStack itemStack, Level level, BlockPos pos) {
        return false;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static InteractionResult applyBonemeal(final UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos relative = pos.relative(context.getClickedFace());
        ItemStack boneMealStack = context.getItemInHand();
        if (growCrop(boneMealStack, level, pos)) {
            if (!level.isClientSide()) {
                boneMealStack.causeUseVibration(context.getPlayer(), GameEvent.ITEM_INTERACT_FINISH);
                level.levelEvent(1505, pos, 15);
                return InteractionResult.SUCCESS_SERVER;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            BlockState clickedState = level.getBlockState(pos);
            boolean solidBlockFace = clickedState.isFaceSturdy(level, pos, context.getClickedFace());
            if (solidBlockFace && growWaterPlant(boneMealStack, level, relative, context.getClickedFace())) {
                if (!level.isClientSide()) {
                    boneMealStack.causeUseVibration(context.getPlayer(), GameEvent.ITEM_INTERACT_FINISH);
                    level.levelEvent(1505, relative, 15);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
