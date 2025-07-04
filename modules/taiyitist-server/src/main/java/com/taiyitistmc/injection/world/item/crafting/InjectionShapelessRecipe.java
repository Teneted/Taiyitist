package com.taiyitistmc.injection.world.item.crafting;

import org.bukkit.NamespacedKey;

public interface InjectionShapelessRecipe {

    default org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        throw new IllegalStateException("Not implemented");
    }
}
