package org.celestial_artistry.taiyitist.injection.world.entity;

public interface InjectionAreaEffectCloud {

    default void refreshEffects() {
    }

    default String getPotionType() {
        return null;
    }

    default void setPotionType(String string) {
    }
}
