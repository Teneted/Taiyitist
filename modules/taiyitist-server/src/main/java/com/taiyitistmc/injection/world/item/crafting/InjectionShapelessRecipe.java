package com.taiyitistmc.injection.world.item.crafting;

public interface InjectionShapelessRecipe {

    default org.bukkit.inventory.Recipe toBukkitRecipe() {
        return null;
    }
}
