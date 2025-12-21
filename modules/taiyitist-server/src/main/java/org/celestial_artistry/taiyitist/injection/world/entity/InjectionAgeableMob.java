package org.celestial_artistry.taiyitist.injection.world.entity;

public interface InjectionAgeableMob {

    default boolean bridge$ageLocked() {
        return false;
    }

    default void taiyitist$setAgeLocked(boolean ageLocked) {
    }
}
