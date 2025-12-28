package org.teneted.taiyitist.injection.world.entity.monster;

public interface InjectionSlime {

    default boolean canWander() {
        return false;
    }

    default void setWander(boolean canWander) {
    }
}
