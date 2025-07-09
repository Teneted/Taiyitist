package com.taiyitistmc.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class MixinEnderEyeItem {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$cancelEntityAdd(Level instance, Entity entity) {
        return false;
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
            shift = At.Shift.AFTER), cancellable = true)
    private void taiyitist$handleAddEntity(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir, @Local EyeOfEnder eyeOfEnder) {
        // CraftBukkit start
        if (!level.addFreshEntity(eyeOfEnder)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
        // CraftBukkit end
    }
}
