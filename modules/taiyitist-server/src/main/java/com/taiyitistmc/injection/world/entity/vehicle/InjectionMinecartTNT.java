package com.taiyitistmc.injection.world.entity.vehicle;

public interface InjectionMinecartTNT {

    default boolean bridge$isIncendiary() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setIsIncendiary(boolean isIncendiary) {
        throw new IllegalStateException("Not implemented");
    }
}
