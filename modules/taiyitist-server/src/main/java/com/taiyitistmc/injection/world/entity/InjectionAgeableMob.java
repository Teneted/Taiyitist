package com.taiyitistmc.injection.world.entity;

public interface InjectionAgeableMob {

    default boolean bridge$ageLocked() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setAgeLocked(boolean ageLocked) {
        throw new IllegalStateException("Not implemented");
    }
}
