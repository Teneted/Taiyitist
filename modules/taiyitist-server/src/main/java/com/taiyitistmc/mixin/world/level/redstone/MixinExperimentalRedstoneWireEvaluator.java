package com.taiyitistmc.mixin.world.level.redstone;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.ExperimentalRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(ExperimentalRedstoneWireEvaluator.class)
public abstract class MixinExperimentalRedstoneWireEvaluator extends RedstoneWireEvaluator {

    @Shadow
    private static Orientation getInitialOrientation(Level level, @Nullable Orientation orientation) {
        return null;
    }

    @Shadow protected abstract void calculateCurrentChanges(Level level, BlockPos blockPos, Orientation orientation);

    @Shadow @Final private Object2IntMap<BlockPos> updatedWires;

    @Shadow
    private static int unpackPower(int i) {
        return 0;
    }

    @Shadow protected abstract void causeNeighborUpdates(Level level);

    // Taiyitist - throw out to compatible with mixins of mods
    @Unique
    private AtomicInteger oldPower = new AtomicInteger();
    @Unique
    private AtomicReference<BlockRedstoneEvent> event = new AtomicReference<>();

    protected MixinExperimentalRedstoneWireEvaluator(RedStoneWireBlock redStoneWireBlock) {
        super(redStoneWireBlock);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public void updatePowerStrength(Level level, BlockPos blockPos, BlockState blockState, @Nullable Orientation orientation, boolean bl) {
        Orientation orientation2 = getInitialOrientation(level, orientation);
        this.calculateCurrentChanges(level, blockPos, orientation2);
        ObjectIterator<Object2IntMap.Entry<BlockPos>> objectIterator = this.updatedWires.object2IntEntrySet().iterator();

        for(boolean bl2 = true; objectIterator.hasNext(); bl2 = false) {
            Object2IntMap.Entry<BlockPos> entry = (Object2IntMap.Entry)objectIterator.next();
            BlockPos blockPos2 = (BlockPos)entry.getKey();
            int i = entry.getIntValue();
            int j = unpackPower(i);
            BlockState blockState2 = level.getBlockState(blockPos2);
            // CraftBukkit start
            oldPower.set(blockState.getValue(RedStoneWireBlock.POWER));
            if (oldPower.get() != j) {
                event.set(new BlockRedstoneEvent(CraftBlock.at(level, blockPos2), oldPower.get(), j));
                level.getCraftServer().getPluginManager().callEvent(event.get());

                j = event.get().getNewCurrent();
            }
            if (blockState2.is(this.wireBlock) && oldPower.get() != j) {
                // CraftBukkit end
                int k = 2;
                if (!bl || !bl2) {
                    k |= 128;
                }

                level.setBlock(blockPos2, (BlockState)blockState2.setValue(RedStoneWireBlock.POWER, j), k);
            } else {
                objectIterator.remove();
            }
        }

        this.causeNeighborUpdates(level);
    }
}
