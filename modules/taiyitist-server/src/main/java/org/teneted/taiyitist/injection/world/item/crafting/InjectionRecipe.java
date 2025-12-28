package org.teneted.taiyitist.injection.world.item.crafting;

public interface InjectionRecipe {

    default org.bukkit.inventory.Recipe toBukkitRecipe() {
        return null;
    }
}
