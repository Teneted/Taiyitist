package com.taiyitistmc.injection.world.entity.vehicle;

import org.bukkit.util.Vector;

public interface InjectionAbstractMinecart {

    default double bridge$maxSpeed() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxSpeed(double maxSpeed) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$slowWhenEmpty() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setSlowWhenEmpty(boolean slowWhenEmpty) {
        throw new IllegalStateException("Not implemented");
    }

    default Vector getFlyingVelocityMod() {
        throw new IllegalStateException("Not implemented");
    }

    default void setFlyingVelocityMod(Vector flying) {
        throw new IllegalStateException("Not implemented");
    }

    default Vector getDerailedVelocityMod() {
        throw new IllegalStateException("Not implemented");
    }

    default void setDerailedVelocityMod(Vector derailed) {
        throw new IllegalStateException("Not implemented");
    }
}
