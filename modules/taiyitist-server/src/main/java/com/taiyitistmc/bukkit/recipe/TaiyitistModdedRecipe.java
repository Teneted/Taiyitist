package com.taiyitistmc.bukkit.recipe;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public class TaiyitistModdedRecipe extends CraftingRecipe implements CraftRecipe {

    private final Recipe recipe;

    public TaiyitistModdedRecipe(NamespacedKey key, ItemStack result, Recipe recipe) {
        super(key, result);
        this.recipe = recipe;
    }

    @Override
    public void addToCraftingManager() {
        BukkitMethodHooks.getServer().getRecipeManager().addRecipe(new RecipeHolder(CraftRecipe.toMinecraft(this.getKey()), this.recipe));
    }

    @Override
    public @NotNull CraftingBookCategory getCategory() {
        return CraftingBookCategory.valueOf(recipe.recipeBookCategory().toString());
    }
}
