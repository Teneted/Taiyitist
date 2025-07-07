package com.taiyitistmc.injection.world.level;

public interface InjectionExplosion {

    default float bridge$getYield() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setYield(float yield) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$wasCanceled() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setWasCanceled(boolean wasCanceled) {
        throw new IllegalStateException("Not implemented");
    }
}
