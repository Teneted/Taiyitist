package com.taiyitistmc.mixin.world.level.redstone;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(DefaultRedstoneWireEvaluator.class)
public abstract class MixinDefaultRedstoneWireEvaluator extends RedstoneWireEvaluator {

    @Shadow protected abstract int calculateTargetStrength(Level level, BlockPos blockPos);

    // Taiyitist - throw out to compatible with mixins of mods
    @Unique
    private AtomicInteger oldPower = new AtomicInteger();
    @Unique
    private AtomicReference<BlockRedstoneEvent> event = new AtomicReference<>();

    protected MixinDefaultRedstoneWireEvaluator(RedStoneWireBlock redStoneWireBlock) {
        super(redStoneWireBlock);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public void updatePowerStrength(Level level, BlockPos blockPos, BlockState blockState, @Nullable Orientation orientation, boolean bl) {
        int i = this.calculateTargetStrength(level, blockPos);
        // CraftBukkit start
        oldPower.set(blockState.getValue(RedStoneWireBlock.POWER));
        if (oldPower.get() != i) {
            event.set(new BlockRedstoneEvent(CraftBlock.at(level, blockPos), oldPower.get(), i));
            level.getCraftServer().getPluginManager().callEvent(event.get());

            i = event.get().getNewCurrent();
        }
        if (oldPower.get() != i) {
            // CraftBukkit end
            if (level.getBlockState(blockPos) == blockState) {
                level.setBlock(blockPos, (BlockState)blockState.setValue(RedStoneWireBlock.POWER, i), 2);
            }

            Set<BlockPos> set = Sets.newHashSet();
            set.add(blockPos);
            Direction[] var8 = Direction.values();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Direction direction = var8[var10];
                set.add(blockPos.relative(direction));
            }

            Iterator var12 = set.iterator();

            while(var12.hasNext()) {
                BlockPos blockPos2 = (BlockPos)var12.next();
                level.updateNeighborsAt(blockPos2, this.wireBlock);
            }
        }

    }
}
