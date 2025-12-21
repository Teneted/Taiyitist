package org.celestial_artistry.taiyitist.injection.world.entity.decoration;

public interface InjectionArmorStand {

    default boolean bridge$canMove() {
        return false;
    }

    default void taiyitist$setCanMove(boolean canMove) {

    }
}
