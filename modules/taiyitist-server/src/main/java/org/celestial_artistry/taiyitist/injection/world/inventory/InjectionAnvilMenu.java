package org.celestial_artistry.taiyitist.injection.world.inventory;

public interface InjectionAnvilMenu {

    default int bridge$getDeniedCost() {
        return 0;
    }

    default int bridge$maximumRepairCost() {
        return 0;
    }

    default void taiyitist$setMaximumRepairCost(int maximumRepairCost) {

    }
}
