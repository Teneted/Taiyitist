package org.teneted.taiyitist.injection.world.inventory;

public interface InjectionAnvilMenu {

    default int bridge$getDeniedCost() {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$maximumRepairCost() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaximumRepairCost(int maximumRepairCost) {
        throw new IllegalStateException("Not implemented");
    }
}
