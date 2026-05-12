package org.teneted.taiyitist.mixin.world.food;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.food.InjectionFoodData;

@Mixin(FoodData.class)
public class MixinFoodData implements InjectionFoodData {
}
