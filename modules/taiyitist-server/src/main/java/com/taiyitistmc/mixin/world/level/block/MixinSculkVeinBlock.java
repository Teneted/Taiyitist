package com.taiyitistmc.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkVeinBlock.class)
public abstract class MixinSculkVeinBlock extends MultifaceSpreadeableBlock implements SculkBehaviour {

    @Shadow @Final private MultifaceSpreader veinSpreader;

    public MixinSculkVeinBlock(Properties properties) {
        super(properties);
    }

    @Redirect(method = "attemptUseCharge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SculkVeinBlock;attemptPlaceSculk(Lnet/minecraft/world/level/block/SculkSpreader;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z"))
    private boolean taiyitist$addSourceBlock(SculkVeinBlock instance, SculkSpreader sculkSpreader, LevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource) {
        return attemptPlaceSculk(sculkSpreader, levelAccessor, blockPos, randomSource, blockPos);
    }

    private boolean attemptPlaceSculk(SculkSpreader sculkSpreader, LevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource, BlockPos sourceBlock) { // CraftBukkit
        BlockState blockState = levelAccessor.getBlockState(blockPos);
        TagKey<Block> tagKey = sculkSpreader.replaceableBlocks();

        for (Direction direction : Direction.allShuffled(randomSource)) {
            if (hasFace(blockState, direction)) {
                BlockPos blockPos2 = blockPos.relative(direction);
                BlockState blockState2 = levelAccessor.getBlockState(blockPos2);
                if (blockState2.is(tagKey)) {
                    BlockState blockState3 = Blocks.SCULK.defaultBlockState();
                    levelAccessor.setBlock(blockPos2, blockState3, 3);
                    // CraftBukkit start - Call BlockSpreadEvent
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockSpreadEvent(levelAccessor, sourceBlock, blockPos2, blockState2, 3)) {
                        return false;
                    }
                    // CraftBukkit end
                    Block.pushEntitiesUp(blockState2, blockState3, levelAccessor, blockPos2);
                    levelAccessor.playSound((Entity) null, blockPos2, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.veinSpreader.spreadAll(blockState3, levelAccessor, blockPos2, sculkSpreader.isWorldGeneration());
                    Direction direction2 = direction.getOpposite();

                    for (Direction direction3 : DIRECTIONS) {
                        if (direction3 != direction2) {
                            BlockPos blockPos3 = blockPos2.relative(direction3);
                            BlockState blockState4 = levelAccessor.getBlockState(blockPos3);
                            if (blockState4.is(this)) {
                                this.onDischarged(levelAccessor, blockState4, blockPos3, randomSource);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
