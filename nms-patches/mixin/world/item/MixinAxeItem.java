package com.taiyitistmc.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class MixinAxeItem {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getItemInHand()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private void taiyitist$callEntityChangeBlockEvent(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, @Local BlockPos blockPos, @Local Player player, @Local Optional<BlockState> optional) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(player, blockPos, optional.get())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
