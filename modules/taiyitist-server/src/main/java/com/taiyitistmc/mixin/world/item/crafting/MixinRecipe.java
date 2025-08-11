package com.taiyitistmc.mixin.world.item.crafting;

import com.taiyitistmc.bukkit.recipe.TaiyitistModdedRecipe;
import com.taiyitistmc.injection.world.item.crafting.InjectionRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Recipe.class)
public interface MixinRecipe extends InjectionRecipe {

    @Shadow String group();

    @Override
    default org.bukkit.inventory.Recipe toBukkitRecipe(org.bukkit.NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(ItemStack.EMPTY);

        TaiyitistModdedRecipe recipe = new TaiyitistModdedRecipe(id, result, ((Recipe) (Object) this));
        recipe.setGroup(this.group());

        return recipe;
    }
}
