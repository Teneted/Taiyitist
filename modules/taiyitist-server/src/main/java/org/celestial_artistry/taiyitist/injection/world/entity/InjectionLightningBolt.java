package org.celestial_artistry.taiyitist.injection.world.entity;

public interface InjectionLightningBolt {

    default boolean bridge$isSilent() {
        return false;
    }

    default void taiyitist$setIsSilent(boolean isSilent) {
    }
}
