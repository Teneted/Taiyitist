package com.taiyitistmc.mixin.world.item.crafting;

import com.taiyitistmc.injection.world.item.crafting.InjectionShapedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ShapedRecipe.class)
public abstract class MixinShapedRecipe implements CraftingRecipe, InjectionShapedRecipe {

    @Shadow @Final private ItemStack result;

    @Shadow @Final private String group;

    @Shadow @Final private ShapedRecipePattern pattern;

    @Override
    public org.bukkit.inventory.ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapedRecipe recipe = new CraftShapedRecipe(id, result, ((ShapedRecipe) (Object) this));
        recipe.setGroup(this.group);
        recipe.setCategory(CraftRecipe.getCategory(this.category()));

        switch (this.pattern.height()) {
            case 1:
                switch (this.pattern.width()) {
                    case 1:
                        recipe.shape("a");
                        break;
                    case 2:
                        recipe.shape("ab");
                        break;
                    case 3:
                        recipe.shape("abc");
                        break;
                }
                break;
            case 2:
                switch (this.pattern.width()) {
                    case 1:
                        recipe.shape("a","b");
                        break;
                    case 2:
                        recipe.shape("ab","cd");
                        break;
                    case 3:
                        recipe.shape("abc","def");
                        break;
                }
                break;
            case 3:
                switch (this.pattern.width()) {
                    case 1:
                        recipe.shape("a","b","c");
                        break;
                    case 2:
                        recipe.shape("ab","cd","ef");
                        break;
                    case 3:
                        recipe.shape("abc","def","ghi");
                        break;
                }
                break;
        }
        char c = 'a';
        for (Optional<Ingredient> list : this.pattern.ingredients()) {
            RecipeChoice choice = CraftRecipe.toBukkit(list);
            if (choice != null) {
                recipe.setIngredient(c, choice);
            }

            c++;
        }
        return recipe;
    }
    // CraftBukkit end
}
