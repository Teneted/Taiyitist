package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionShapelessRecipe;

@Mixin(ShapelessRecipe.class)
public class MixinShapelessRecipe implements InjectionShapelessRecipe {
}
