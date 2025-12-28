package org.teneted.taiyitist.injection.world.entity;

public interface InjectionAgeableMob {

    default boolean bridge$ageLocked() {
        return false;
    }

    default void taiyitist$setAgeLocked(boolean ageLocked) {
    }
}
