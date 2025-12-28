package org.teneted.taiyitist.injection.world.level;

public interface InjectionExplosion {

    default boolean bridge$wasCanceled() {
        return false;
    }

    default void taiyitist$setWasCanceled(boolean wasCanceled) {
    }
}
