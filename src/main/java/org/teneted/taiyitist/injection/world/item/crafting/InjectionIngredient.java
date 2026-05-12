package org.teneted.taiyitist.injection.world.item.crafting;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface InjectionIngredient {

    default boolean bridge$exact() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setItemStacks(List<ItemStack> itemStacks) {
        throw new IllegalStateException("Not implemented");
    }

    default List<ItemStack> itemStacks() {
        throw new IllegalStateException("Not implemented");
    }
}
