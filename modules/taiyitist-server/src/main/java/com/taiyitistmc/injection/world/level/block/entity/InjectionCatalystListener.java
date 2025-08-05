package com.taiyitistmc.injection.world.level.block.entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Mixin;

public interface InjectionCatalystListener {

    default void taiyitist$setLevel(Level level) {
        throw new IllegalStateException("Not implemented");
    }
}
