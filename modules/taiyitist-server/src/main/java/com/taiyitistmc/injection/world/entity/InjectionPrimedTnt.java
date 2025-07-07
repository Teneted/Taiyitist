package com.taiyitistmc.injection.world.entity;

public interface InjectionPrimedTnt {

    default float bridge$yield() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setYield(float yield) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$isIncendiary() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setIsIncendiary(boolean isIncendiary) {
        throw new IllegalStateException("Not implemented");
    }
}
