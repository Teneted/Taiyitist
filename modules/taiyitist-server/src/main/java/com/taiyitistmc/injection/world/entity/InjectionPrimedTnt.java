package com.taiyitistmc.injection.world.entity;

public interface InjectionPrimedTnt {

    default float bridge$yield() {
        return 0;
    }

    default void taiyitist$setYield(float yield) {
    }

    default boolean bridge$isIncendiary() {
        return false;
    }

    default void taiyitist$setIsIncendiary(boolean isIncendiary) {
    }
}
