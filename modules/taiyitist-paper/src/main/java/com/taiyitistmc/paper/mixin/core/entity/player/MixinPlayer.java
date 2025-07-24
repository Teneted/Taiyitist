package com.taiyitistmc.paper.mixin.core.entity.player;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(Player.class)
public class MixinPlayer {

    protected boolean tryReadyArrow(ItemStack bow, ItemStack itemstack) {
        return !(((Player) (Object) this) instanceof ServerPlayer) ||
                new PlayerReadyArrowEvent(
                        ((ServerPlayer) (Object) this).getBukkitEntity(),
                        CraftItemStack.asCraftMirror(bow),
                        CraftItemStack.asCraftMirror(itemstack)
                ).callEvent();
    }

    @Redirect(method = "getProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getAllSupportedProjectiles()Ljava/util/function/Predicate;"))
    private Predicate<ItemStack> taiyitist$tryReadyArrow(ProjectileWeaponItem instance, @Local(ordinal = 0, argsOnly = true) ItemStack itemStack) {
        return ((ProjectileWeaponItem)itemStack.getItem()).getAllSupportedProjectiles().and(item -> tryReadyArrow(itemStack, item)); // Paper - PlayerReadyArrowEvent
    }
}
