package com.taiyitistmc.injection.world.entity;

public interface InjectionAgeableMob {

    default boolean bridge$ageLocked() {
        return false;
    }

    default void banner$setAgeLocked(boolean ageLocked) {
    }
}
