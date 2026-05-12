package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionRecipeManager;

@Mixin(RecipeManager.class)
public class MixinRecipeManager implements InjectionRecipeManager {
}
