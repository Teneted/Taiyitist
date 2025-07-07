package com.taiyitistmc.injection.world.entity.vehicle;

public interface InjectionAbstractBoat {

    default double bridge$maxSpeed() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxSpeed(double maxSpeed) {
        throw new IllegalStateException("Not implemented");
    }

    default double bridge$occupiedDeceleration() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setOccupiedDeceleration(double occupiedDeceleration) {
        throw new IllegalStateException("Not implemented");
    }

    default double bridge$unoccupiedDeceleration() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setUnoccupiedDeceleration(double occupiedDeceleration) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$landBoats() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setLandBoats(boolean landBoats) {
        throw new IllegalStateException("Not implemented");
    }
}
