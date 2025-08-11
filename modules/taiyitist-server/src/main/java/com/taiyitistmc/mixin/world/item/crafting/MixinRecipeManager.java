package com.taiyitistmc.mixin.world.item.crafting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.BukkitMethodHooks;
import com.taiyitistmc.injection.world.item.crafting.InjectionRecipeManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(RecipeManager.class)
public abstract class MixinRecipeManager implements InjectionRecipeManager {

    @Shadow
    public RecipeMap recipes;

    @Shadow public abstract void finalizeRecipeLoading(FeatureFlagSet featureFlagSet);

    @Override
    public void addRecipe(RecipeHolder<?> irecipe) {
        this.recipes.addRecipe(irecipe);
        finalizeRecipeLoading();
    }

    @Override
    public void clearRecipes() {
        this.recipes = RecipeMap.create(Collections.emptyList());
        finalizeRecipeLoading();
    }

    @Override
    public boolean removeRecipe(ResourceKey<Recipe<?>> minecraft) {
        boolean removed = this.recipes.removeRecipe(minecraft);
        if (removed) {
            finalizeRecipeLoading();
        }

        return removed;
    }

    private FeatureFlagSet featureflagset;

    @Override
    public void finalizeRecipeLoading() {
        if (featureflagset != null) {
            finalizeRecipeLoading(featureflagset);

            BukkitMethodHooks.getServer().getPlayerList().reloadRecipes();
        }
    }

    @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
    private void taiyitist$setFlag(FeatureFlagSet featureFlagSet, CallbackInfo ci) {
        this.featureflagset = featureFlagSet;
    }

    @ModifyReturnValue(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("RETURN"))
    private <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> taiyitist$resetRecipe(Optional<RecipeHolder<T>> original, @Local(argsOnly = true) RecipeType<T> recipeType, @Local(argsOnly = true) I recipeInput, @Local(argsOnly = true) Level level) {
        // CraftBukkit start
        List<RecipeHolder<T>> list = this.recipes.getRecipesFor(recipeType, recipeInput, level).toList();
        return (list.isEmpty()) ? Optional.empty() : Optional.of(list.getLast()); // CraftBukkit - SPIGOT-4638: last recipe gets priority
        // CraftBukkit end
    }
}
