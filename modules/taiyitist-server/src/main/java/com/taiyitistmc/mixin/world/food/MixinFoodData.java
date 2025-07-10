package com.taiyitistmc.mixin.world.food;

import com.taiyitistmc.injection.world.food.InjectionFoodData;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoodData.class)
public class MixinFoodData implements InjectionFoodData {

    // CraftBukkit start
    public int saturatedRegenRate = 10;
    public int unsaturatedRegenRate = 80;
    public int starvationRate = 80;
    // CraftBukkit end


    @Override
    public int bridge$saturatedRegenRate() {
        return saturatedRegenRate;
    }

    @Override
    public void taiyitist$setSaturatedRegenRate(int saturatedRegenRate) {
        this.saturatedRegenRate = saturatedRegenRate;
    }

    @Override
    public int bridge$unsaturatedRegenRate() {
        return unsaturatedRegenRate;
    }

    @Override
    public void taiyitist$setUnsaturatedRegenRate(int unsaturatedRegenRate) {
        this.unsaturatedRegenRate = unsaturatedRegenRate;
    }

    @Override
    public int bridge$starvationRate() {
        return starvationRate;
    }

    @Override
    public void taiyitist$setStarvationRate(int starvationRate) {
        this.starvationRate = starvationRate;
    }
}
