package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionIngredient;

@Mixin(Ingredient.class)
public class MixinIngredient implements InjectionIngredient {
}
