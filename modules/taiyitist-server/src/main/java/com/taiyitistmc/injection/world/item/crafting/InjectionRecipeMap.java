package com.taiyitistmc.injection.world.item.crafting;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface InjectionRecipeMap {

    default void addRecipe(RecipeHolder<?> irecipe) {

    }

    default  boolean removeRecipe(ResourceKey<Recipe<?>> mcKey) {
        throw new IllegalStateException("Not implemented");
    }
}
