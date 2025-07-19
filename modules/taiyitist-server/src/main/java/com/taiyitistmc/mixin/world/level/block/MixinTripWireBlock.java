package com.taiyitistmc.mixin.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TripWireBlock.class)
public abstract class MixinTripWireBlock extends Block {

    @Shadow
    @Final
    public static BooleanProperty POWERED;

    public MixinTripWireBlock(Properties properties) {
        super(properties);
    }

    @Shadow
    protected abstract void updateSource(Level level, BlockPos pos, BlockState state);

    @Shadow @Final public static BooleanProperty ATTACHED;

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    private void checkPressed(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        boolean bl = (Boolean)blockState.getValue(POWERED);
        boolean bl2 = false;
        List<? extends Entity> list = level.getEntities(null, blockState.getShape(level, blockPos).bounds().move(blockPos));
        if (!list.isEmpty()) {

            for (Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers()) {
                    bl2 = true;
                    break;
                }
            }
        }

        // CraftBukkit start - Call interact even when triggering connected tripwire
        if (bl != bl2 && bl2 && (Boolean)blockState.getValue(ATTACHED)) {
            org.bukkit.World bworld = level.getWorld();
            org.bukkit.plugin.PluginManager manager = level.getCraftServer().getPluginManager();
            org.bukkit.block.Block block = bworld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            boolean allowed = false;

            // If all of the events are cancelled block the tripwire trigger, else allow
            for (Object object : list) {
                if (object != null) {
                    org.bukkit.event.Cancellable cancellable;

                    if (object instanceof Player) {
                        cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((Player) object, org.bukkit.event.block.Action.PHYSICAL, blockPos, null, null, null);
                    } else if (object instanceof Entity) {
                        cancellable = new EntityInteractEvent(((Entity) object).getBukkitEntity(), block);
                        manager.callEvent((EntityInteractEvent) cancellable);
                    } else {
                        continue;
                    }

                    if (!cancellable.isCancelled()) {
                        allowed = true;
                        break;
                    }
                }
            }

            if (!allowed) {
                return;
            }
        }
        // CraftBukkit end

        if (bl2 != bl) {
            blockState = blockState.setValue(POWERED, bl2);
            level.setBlock(blockPos, blockState, 3);
            this.updateSource(level, blockPos, blockState);
        }

        if (bl2) {
            level.scheduleTick(new BlockPos(blockPos), this, 10);
        }

    }
}
