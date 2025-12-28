package org.teneted.taiyitist.injection.world.item.crafting;

public interface InjectionIngredient {

    default boolean bridge$exact() {
        return false;
    }

    default void taiyitist$setExact(boolean exact) {
    }
}
