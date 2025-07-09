package com.taiyitistmc.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CakeBlock.class)
public class MixinCakeBlock {

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void taiyitist$FoodLevelChangeEvent(FoodData instance, int i, float f, @Local(argsOnly = true) Player player) {
        // CraftBukkit start
        // entityhuman.getFoodData().eat(2, 0.1F);
        int oldFoodLevel = player.getFoodData().foodLevel;

        org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(player, 2 + oldFoodLevel);

        if (!event.isCancelled()) {
            player.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 0.1F);
        }

        ((net.minecraft.server.level.ServerPlayer) player).getBukkitEntity().sendHealthUpdate();
        // CraftBukkit end
    }
}
