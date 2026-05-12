package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionShapedRecipe;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe implements InjectionShapedRecipe {
}
