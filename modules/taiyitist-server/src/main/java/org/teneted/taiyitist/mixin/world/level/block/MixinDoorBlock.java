package org.teneted.taiyitist.mixin.world.level.block;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DoorBlock.class)
public abstract class MixinDoorBlock extends Block{

    @Shadow @Final public static EnumProperty<DoubleBlockHalf> HALF;

    @Shadow @Final public static BooleanProperty POWERED;

    @Shadow @Final public static BooleanProperty OPEN;

    @Shadow protected abstract void playSound(@Nullable Entity source, Level level, BlockPos pos, boolean isOpening);

    public MixinDoorBlock(Properties properties) {
        super(properties);
    }

    @Unique
    private final AtomicInteger taiyitist$power = new AtomicInteger();
    @Unique
    private final AtomicInteger taiyitist$oldPower = new AtomicInteger();
    @Unique
    private org.bukkit.block.Block bukkitBlock;
    @Unique
    private BlockRedstoneEvent eventRedstone;

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        // CraftBukkit start
        {
            BlockPos otherHalf = pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
            World bworld = level.getWorld();
            bukkitBlock = bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            org.bukkit.block.Block blockTop = bworld.getBlockAt(otherHalf.getX(), otherHalf.getY(), otherHalf.getZ());

            int power = bukkitBlock.getBlockPower();
            int powerTop = blockTop.getBlockPower();
            if (powerTop > power) power = powerTop;
            taiyitist$power.set(power);
            int oldPower = (Boolean) state.getValue(POWERED) ? 15 : 0;
            taiyitist$oldPower.set(oldPower);
        }
        if (taiyitist$oldPower.get() == 0 ^ taiyitist$power.get() == 0) {
            eventRedstone = new BlockRedstoneEvent(bukkitBlock, taiyitist$oldPower.get(), taiyitist$power.get());
            Bukkit.getPluginManager().callEvent(eventRedstone);
            boolean bl = eventRedstone.getNewCurrent() > 0;
            if (bl != (Boolean) state.getValue(OPEN)) {
                this.playSound((Entity) null, level, pos, bl);
                level.gameEvent((Entity) null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            level.setBlock(pos, (BlockState) ((BlockState) state.setValue(POWERED, bl)).setValue(OPEN, bl), 2);
        }
    }
}
