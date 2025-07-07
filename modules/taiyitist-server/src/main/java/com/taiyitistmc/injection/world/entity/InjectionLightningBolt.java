package com.taiyitistmc.injection.world.entity;

public interface InjectionLightningBolt {

    default boolean bridge$isSilent() {
        return false;
    }

    default void taiyitist$setIsSilent(boolean isSilent) {
    }
}
