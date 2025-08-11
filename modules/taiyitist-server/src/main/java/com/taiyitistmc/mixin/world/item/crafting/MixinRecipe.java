package com.taiyitistmc.mixin.world.item.crafting;

import com.taiyitistmc.injection.world.item.crafting.InjectionRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Recipe.class)
public interface MixinRecipe extends InjectionRecipe {

    @Override
    org.bukkit.inventory.Recipe toBukkitRecipe(org.bukkit.NamespacedKey id); // CraftBukkit
}
