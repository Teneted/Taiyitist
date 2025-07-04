package com.taiyitistmc.injection.world.entity.decoration;

public interface InjectionArmorStand {

    default boolean bridge$canMove() {
        throw new IllegalStateException("Not implemented");
    }

    default void banner$setCanMove(boolean canMove) {
        throw new IllegalStateException("Not implemented");
    }
}
