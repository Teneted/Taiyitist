package org.teneted.taiyitist.injection.world.item.crafting;

import org.bukkit.NamespacedKey;

public interface InjectionShapedRecipe extends InjectionRecipe {

    @Override
    default org.bukkit.inventory.ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        throw new IllegalStateException("Not implemented");
    }

}
