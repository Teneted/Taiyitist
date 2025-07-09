package com.taiyitistmc.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlock.class)
public abstract class MixinSculkSensorBlock extends BaseEntityBlock {

    protected MixinSculkSensorBlock(Properties properties) {
        super(properties);
    }

    @Inject(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), cancellable = true)
    private void taiyitist$entityInteractEvent(Level level, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.event.Cancellable cancellable;
        if (entity instanceof Player) {
            cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((Player) entity, org.bukkit.event.block.Action.PHYSICAL, blockPos, null, null, null);
        } else {
            cancellable = new org.bukkit.event.entity.EntityInteractEvent(entity.getBukkitEntity(), level.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            level.getCraftServer().getPluginManager().callEvent((org.bukkit.event.entity.EntityInteractEvent) cancellable);
        }
        if (cancellable.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "deactivate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private static void taiyitist$redstoneEvent(Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, blockPos), blockState.getValue(SculkSensorBlock.POWER), 0);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0) {
            level.setBlock(blockPos, blockState.setValue(SculkSensorBlock.POWER, eventRedstone.getNewCurrent()), 3);
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "activate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void taiyitist$redstoneEvent0(Entity entity, Level level, BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, blockPos), blockState.getValue(SculkSensorBlock.POWER), i);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() <= 0) {
            return;
        }
        i = eventRedstone.getNewCurrent();
        // CraftBukkit end
    }

    @Override
    public int getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean flag) {
        if (flag) {
            return this.taiyitist$tryDropExperience(world, blockPos, itemStack, ConstantInt.of(5));
        }
        return 0;
    }
}
