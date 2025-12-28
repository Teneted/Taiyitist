package org.teneted.taiyitist.injection.world.entity.vehicle;

public interface InjectionBoat {

    default double bridge$maxSpeed() {
        return 0;
    }

    default void taiyitist$setMaxSpeed(double maxSpeed) {
    }

    default double bridge$occupiedDeceleration() {
        return 0;
    }

    default void taiyitist$setOccupiedDeceleration(double occupiedDeceleration) {
    }

    default double bridge$unoccupiedDeceleration() {
        return 0;
    }

    default void taiyitist$setUnoccupiedDeceleration(double occupiedDeceleration) {
    }

    default boolean bridge$landBoats() {
        return false;
    }

    default void taiyitist$setLandBoats(boolean landBoats) {
    }
}
