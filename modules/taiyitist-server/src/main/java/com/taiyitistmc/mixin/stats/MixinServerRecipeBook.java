package com.taiyitistmc.mixin.stats;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerRecipeBook.class)
public class MixinServerRecipeBook {

    @ModifyExpressionValue(method = "addRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;isSpecial()Z"))
    private boolean taiyitist$fireRecipeListUpdateEvent(boolean original, @Local(argsOnly = true) ServerPlayer serverPlayer, @Local ResourceKey<Recipe<?>> resourceKey) {
        return original && CraftEventFactory.handlePlayerRecipeListUpdateEvent(serverPlayer, resourceKey.location());
    }

    @ModifyExpressionValue(method = "addRecipes", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean taiyitist$fixConn(boolean original, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        return original && serverPlayer.connection != null; // SPIGOT-4478 during PlayerLoginEvent
    }

    @ModifyExpressionValue(method = "removeRecipes", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean taiyitist$fixConn0(boolean original, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        return original && serverPlayer.connection != null; // SPIGOT-4478 during PlayerLoginEvent
    }
}
