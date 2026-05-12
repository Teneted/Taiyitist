package org.teneted.taiyitist.injection.world.entity.monster;

public interface InjectionCreeper {

    default void setPowered(boolean power) {
        throw new IllegalStateException("Not implemented");
    }
}
