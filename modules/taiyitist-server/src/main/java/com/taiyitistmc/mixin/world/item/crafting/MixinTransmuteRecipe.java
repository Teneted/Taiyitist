package com.taiyitistmc.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransmuteRecipe.class)
public abstract class MixinTransmuteRecipe implements CraftingRecipe {

    @Shadow @Final private TransmuteResult result;

    @Shadow @Final private Ingredient input;

    @Shadow @Final private Ingredient material;

    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftTransmuteRecipe(id, CraftRecipe.toBukkit(this.result), CraftRecipe.toBukkit(this.input), CraftRecipe.toBukkit(this.material));
    }
    // CraftBukkit end
}
