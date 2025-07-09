package com.taiyitistmc.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeItem.class)
public class MixinDyeItem {

    @Redirect(method = "interactLivingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;setColor(Lnet/minecraft/world/item/DyeColor;)V"))
    private void taiyitist$colorEvent(Sheep entitysheep, DyeColor dyeColor, @Local(argsOnly = true) Player player, @Cancellable CallbackInfoReturnable<InteractionResult> ci) {
        // CraftBukkit start
        byte bColor = (byte) dyeColor.getId();
        SheepDyeWoolEvent event = new SheepDyeWoolEvent((org.bukkit.entity.Sheep) entitysheep.getBukkitEntity(), org.bukkit.DyeColor.getByWoolData(bColor), (org.bukkit.entity.Player) player.getBukkitEntity());
        entitysheep.level().getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.setReturnValue(InteractionResult.PASS);
        }

        entitysheep.setColor(DyeColor.byId((byte) event.getColor().getWoolData()));
        // CraftBukkit end
    }
}
