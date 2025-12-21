package org.celestial_artistry.taiyitist.mixin.world.item.crafting;

import org.celestial_artistry.taiyitist.bukkit.inventory.recipe.BannerModdedRecipe;
import org.celestial_artistry.taiyitist.injection.world.item.crafting.InjectionRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Recipe.class)
public interface MixinRecipe extends InjectionRecipe {

    @Override
    default org.bukkit.inventory.Recipe toBukkitRecipe() {
        return new BannerModdedRecipe(((Recipe<?>) (Object) this));
    }
}
