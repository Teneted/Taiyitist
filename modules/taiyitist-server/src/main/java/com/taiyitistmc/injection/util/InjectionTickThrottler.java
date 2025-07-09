package com.taiyitistmc.injection.util;

public interface InjectionTickThrottler {

    default boolean isIncrementAndUnderThreshold() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isIncrementAndUnderThreshold(int incrementStep, int threshold) {
        throw new IllegalStateException("Not implemented");
    }
}
