package org.teneted.taiyitist.injection.world.entity;

public interface InjectionLightningBolt {

    default boolean bridge$isSilent() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setIsSilent(boolean isSilent) {
        throw new IllegalStateException("Not implemented");
    }
}
