package org.teneted.taiyitist.injection.world.entity.animal.equine;

public interface InjectionLlama {

    default void setStrengthPublic(final int strength) {
        throw new AssertionError("Not implemented");
    }
}
