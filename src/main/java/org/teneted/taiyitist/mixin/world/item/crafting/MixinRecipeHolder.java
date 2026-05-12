package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionRecipeHolder;

@Mixin(RecipeHolder.class)
public class MixinRecipeHolder implements InjectionRecipeHolder {
}
