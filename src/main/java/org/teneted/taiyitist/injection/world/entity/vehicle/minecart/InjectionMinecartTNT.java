package org.teneted.taiyitist.injection.world.entity.vehicle.minecart;

public interface InjectionMinecartTNT {

    default boolean bridge$isIncendiary() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setIsIncendiary(boolean isIncendiary) {
        throw new IllegalStateException("Not implemented");
    }
}
