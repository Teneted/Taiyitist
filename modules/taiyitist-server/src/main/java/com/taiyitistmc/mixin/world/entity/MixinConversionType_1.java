package com.taiyitistmc.mixin.world.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.injection.world.entity.InjectionConversionType;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/entity/ConversionType$1")
public class MixinConversionType_1 implements InjectionConversionType {

    @Inject(method = "convert", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$removeCause(Mob mob, Mob mob2, ConversionParams conversionParams, CallbackInfo ci, @Local(ordinal = 1) Entity entity2) {
        entity2.pushRemoveCause(EntityRemoveEvent.Cause.TRANSFORMATION);
    }

    @Redirect(method = "convert", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyAndClear()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack taiyitist$donotClear(ItemStack instance) {
        return instance.copy();// CraftBukkit - SPIGOT-7996: don't clear yet
    }

    // CraftBukkit start
    @Override
    public void postConvert(Mob entityinsentient, Mob entityinsentient1, ConversionParams conversionparams) {
        if (conversionparams.keepEquipment()) {
            for (EquipmentSlot enumitemslot : EquipmentSlot.VALUES) {
                ItemStack itemstack = entityinsentient.getItemBySlot(enumitemslot);

                if (!itemstack.isEmpty()) {
                    itemstack.setCount(0); // SPIGOT-7996: clear after conversion
                }
            }
        }
    }
    // CraftBukkit end
}
