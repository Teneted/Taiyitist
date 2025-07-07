package com.taiyitistmc.injection.world.item.crafting;

public interface InjectionIngredient {

    default boolean bridge$exact() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setExact(boolean exact) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isVanilla() {
        throw new IllegalStateException("Not implemented");
    }
}
