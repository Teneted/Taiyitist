package org.celestial_artistry.taiyitist.injection.world.entity.projectile;

public interface InjectionArrow {

    default void refreshEffects() {
    }

    default String getPotionType() {
        return null;
    }

    default void setPotionType(String string) {
    }

    default boolean isTipped() {
        return false;
    }
}
