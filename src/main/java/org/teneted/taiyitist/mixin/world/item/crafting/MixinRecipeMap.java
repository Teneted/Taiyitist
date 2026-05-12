package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionRecipeMap;

@Mixin(RecipeMap.class)
public class MixinRecipeMap implements InjectionRecipeMap {
}
