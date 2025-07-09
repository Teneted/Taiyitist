package com.taiyitistmc.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HangingEntityItem.class)
public class MixinHangingEntityItem {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/HangingEntity;playPlacementSound()V"), cancellable = true)
    private void taiyitist$hangingEvent(UseOnContext useOnContext,
                                        CallbackInfoReturnable<InteractionResult> cir,
                                        @Local(ordinal = 0) BlockPos blockPos,
                                        @Local Direction direction, @Local Level level,
                                        @Local HangingEntity hangingEntity,
                                        @Local ItemStack itemStack) {

        // CraftBukkit start - fire HangingPlaceEvent
        Player who = (useOnContext.getPlayer() == null) ? null : (Player) useOnContext.getPlayer().getBukkitEntity();
        org.bukkit.block.Block blockClicked = level.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        org.bukkit.block.BlockFace blockFace = org.bukkit.craftbukkit.block.CraftBlock.notchToBlockFace(direction);
        org.bukkit.inventory.EquipmentSlot hand = org.bukkit.craftbukkit.CraftEquipmentSlot.getHand(useOnContext.getHand());

        HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) hangingEntity.getBukkitEntity(), who, blockClicked, blockFace, hand, org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemStack));
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
        // CraftBukkit end
    }
}
