package com.taiyitistmc.injection.world.entity.decoration;

public interface InjectionArmorStand {

    default boolean bridge$canMove() {
        return false;
    }

    default void banner$setCanMove(boolean canMove) {

    }
}
