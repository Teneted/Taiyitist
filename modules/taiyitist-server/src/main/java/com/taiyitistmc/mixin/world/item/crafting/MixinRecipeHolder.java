package com.taiyitistmc.mixin.world.item.crafting;

import com.taiyitistmc.injection.world.item.crafting.InjectionRecipeHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeHolder.class)
public abstract class MixinRecipeHolder<T extends Recipe<?>> implements InjectionRecipeHolder {

    @Shadow @Final private T value;

    @Shadow @Final private ResourceKey<Recipe<?>> id;

    // CraftBukkit start
    public final org.bukkit.inventory.Recipe toBukkitRecipe() {
        return this.value.toBukkitRecipe(CraftNamespacedKey.fromMinecraft(this.id.location()));
    }
    // CraftBukkit end
}
