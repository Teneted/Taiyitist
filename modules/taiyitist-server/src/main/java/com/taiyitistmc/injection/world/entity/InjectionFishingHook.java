package com.taiyitistmc.injection.world.entity;

public interface InjectionFishingHook {

    default int bridge$minLureTime() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMinLureTime(int minLureTime) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$maxLureTime() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxLureTime(int maxLureTime) {
        throw new IllegalStateException("Not implemented");
    }

    default float bridge$minLureAngle() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMinLureAngle(float minLureAngle) {
        throw new IllegalStateException("Not implemented");
    }

    default float bridge$maxLureAngle() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxLureAngle(float maxLureAngle) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$rainInfluenced() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setRainInfluenced(boolean rainInfluenced) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$skyInfluenced() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setSkyInfluenced(boolean skyInfluenced) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$minWaitTime() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMinWaitTime(int minWaitTime) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$maxWaitTime() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxWaitTime(int minWaitTime) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$applyLure() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setApplyLure(boolean applyLure) {
        throw new IllegalStateException("Not implemented");
    }
}
