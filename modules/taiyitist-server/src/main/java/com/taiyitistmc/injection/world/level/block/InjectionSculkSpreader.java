package com.taiyitistmc.injection.world.level.block;

import net.minecraft.world.level.Level;

public interface InjectionSculkSpreader {

    default Level bridge$level() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setLevel(Level level) {
        throw new IllegalStateException("Not implemented");
    }
}
