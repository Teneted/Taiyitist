package org.teneted.taiyitist.injection.world.entity;

public interface InjectionFishingHook {

    default int bridge$minLureTime() {
        return 0;
    }

    default void taiyitist$setMinLureTime(int minLureTime) {}

    default int bridge$maxLureTime() {
        return 0;
    }

    default void taiyitist$setMaxLureTime(int maxLureTime) {}

    default float bridge$minLureAngle() {
        return 0;
    }

    default void taiyitist$setMinLureAnglee(float minLureAngle) {}

    default float bridge$maxLureAngle() {
        return 0;
    }

    default void taiyitist$setMaxLureAnglee(float maxLureAngle) {}

    default boolean bridge$rainInfluenced() {
        return false;
    }

    default void taiyitist$setRainInfluenced(boolean rainInfluenced) {}

    default boolean bridge$skyInfluenced() {
        return false;
    }

    default void taiyitist$setSkyInfluenced(boolean skyInfluenced) {}

    default int bridge$minWaitTime() {
        return 0;
    }

    default void taiyitist$setMinWaitTime(int minWaitTime) {}

    default int bridge$maxWaitTime() {
        return 0;
    }

    default void taiyitist$setMaxWaitTime(int minWaitTime) {}

    default boolean bridge$applyLure() {
        return false;
    }

    default void taiyitist$setApplyLure(boolean applyLure) {}
}
