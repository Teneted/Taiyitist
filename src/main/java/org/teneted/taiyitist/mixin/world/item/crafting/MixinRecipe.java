package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionRecipe;

@Mixin(Recipe.class)
public class MixinRecipe implements InjectionRecipe {
}
