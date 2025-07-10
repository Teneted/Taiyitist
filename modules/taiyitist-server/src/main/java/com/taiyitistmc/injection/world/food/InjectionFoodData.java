package com.taiyitistmc.injection.world.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public interface InjectionFoodData {

    default int bridge$saturatedRegenRate() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setSaturatedRegenRate(int saturatedRegenRate) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$unsaturatedRegenRate() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setUnsaturatedRegenRate(int unsaturatedRegenRate) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$starvationRate() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setStarvationRate(int starvationRate) {
        throw new IllegalStateException("Not implemented");
    }

    default void eat(ItemStack itemstack, FoodProperties foodinfo) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushEatStack(ItemStack stack) {
        throw new IllegalStateException("Not implemented");
    }
}
