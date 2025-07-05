package com.taiyitistmc.injection.world.entity;

public interface InjectionLightningBolt {

    default boolean bridge$isSilent() {
        return false;
    }

    default void banner$setIsSilent(boolean isSilent) {
    }
}
