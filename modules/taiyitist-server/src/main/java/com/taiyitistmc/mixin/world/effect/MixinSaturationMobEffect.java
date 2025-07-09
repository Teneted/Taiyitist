package com.taiyitistmc.mixin.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.effect.SaturationMobEffect")
public class MixinSaturationMobEffect {

    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private void taiyitist$fireEatEvent(FoodData instance, int i, float f, @Local Player player) {
        // CraftBukkit start
        int oldFoodLevel = player.getFoodData().foodLevel;
        org.bukkit.event.entity.FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(player, i + 1 + oldFoodLevel);
        if (!event.isCancelled()) {
            player.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 1.0F);
        }

        ((CraftPlayer) player.getBukkitEntity()).sendHealthUpdate();
        // CraftBukkit end
    }
}
