package com.taiyitistmc.mixin.world.food;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import com.taiyitistmc.injection.world.food.InjectionFoodData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoodData.class)
public class MixinFoodData implements InjectionFoodData {

    // CraftBukkit start
    public int saturatedRegenRate = 10;
    public int unsaturatedRegenRate = 80;
    public int starvationRate = 80;
    // CraftBukkit end

    private Player entityhuman;

    @ShadowConstructor
    public void taiyitist$constructor() {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(Player entityhuman) {
        Validate.notNull(entityhuman);
        this.entityhuman = entityhuman;
    }

    @Override
    public Player getEntityhuman() {
        return entityhuman;
    }

    @Override
    public void setEntityhuman(Player entityhuman) {
        this.entityhuman = entityhuman;
    }

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
