package com.taiyitistmc.mixin.core.dispenser;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.BukkitFieldHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxDispenseBehavior.class)
public class MixinShulkerBoxDispenseBehavior {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void taiyitist$dispenseEvent(BlockSource blockSource, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir, @Local BlockPos blockPos) {
        // CraftBukkit start
        org.bukkit.block.Block bukkitBlock = CraftBlock.at(blockSource.level(), blockSource.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        if (!BukkitFieldHooks.isEventFired()) {
            blockSource.level().getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            cir.setReturnValue(itemStack);
        }

        if (!event.getItem().equals(craftItem)) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                idispensebehavior.dispense(blockSource, eventStack);
                cir.setReturnValue(itemStack);
            }
        }
        // CraftBukkit end
    }
}
