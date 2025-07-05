package com.taiyitistmc.mixin.world.item.crafting;

import com.taiyitistmc.bukkit.inventory.recipe.BannerModdedRecipe;
import com.taiyitistmc.injection.world.item.crafting.InjectionRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Recipe.class)
public interface MixinRecipe extends InjectionRecipe {

    @Override
    default org.bukkit.inventory.Recipe toBukkitRecipe() {
        return new BannerModdedRecipe(((Recipe<?>) (Object) this));
    }
}
