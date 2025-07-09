package com.taiyitistmc.injection.world.level.block;

import net.minecraft.core.BlockPos;

public interface InjectionBaseFireBlock {

    default void fireExtinguished(net.minecraft.world.level.LevelAccessor world, BlockPos position) {
        throw new IllegalStateException("Not implemented");
    }
}
