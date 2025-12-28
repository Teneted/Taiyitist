package org.teneted.taiyitist.injection.world.item.crafting;

public interface InjectionShapedRecipe {

    default org.bukkit.inventory.Recipe toBukkitRecipe() {
        return null;
    }

}
