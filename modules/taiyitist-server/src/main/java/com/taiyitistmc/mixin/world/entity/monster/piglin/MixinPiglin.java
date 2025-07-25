package com.taiyitistmc.mixin.world.entity.monster.piglin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.taiyitistmc.injection.world.entity.InjectionPiglin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(Piglin.class)
public class MixinPiglin implements InjectionPiglin {

    // CraftBukkit start - Custom bartering and interest list
    public Set<Item> allowedBarterItems = new HashSet<>();
    public Set<Item> interestItems = new HashSet<>();
    // CraftBukkit end

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        // CraftBukkit start
        ValueOutput.TypedOutputList<String> barterList = valueOutput.list("Bukkit.BarterList", Codec.STRING);
        allowedBarterItems.stream().map(BuiltInRegistries.ITEM::getKey).map(ResourceLocation::toString).forEach(barterList::add);
        ValueOutput.TypedOutputList<String> interestList = valueOutput.list("Bukkit.InterestList", Codec.STRING);
        interestItems.stream().map(BuiltInRegistries.ITEM::getKey).map(ResourceLocation::toString).forEach(interestList::add);
        // CraftBukkit end
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
        // CraftBukkit start
        valueInput.list("Bukkit.BarterList", Codec.STRING).ifPresent((list -> {
            this.allowedBarterItems = list.stream().map(ResourceLocation::tryParse).map(BuiltInRegistries.ITEM::getValue).collect(Collectors.toCollection(HashSet::new));
        }));
        valueInput.list("Bukkit.InterestList", Codec.STRING).ifPresent((list -> {
            this.interestItems = list.stream().map(ResourceLocation::tryParse).map(BuiltInRegistries.ITEM::getValue).collect(Collectors.toCollection(HashSet::new));
        }));
        // CraftBukkit end
    }

    @ModifyExpressionValue(method = "holdInOffHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean taiyitist$checkHoldIn(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
        return original || allowedBarterItems.contains(itemStack.getItem()); // CraftBukkit - Changes to accept custom payment items
    }

    @ModifyExpressionValue(method = "canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0))
    private boolean taiyitist$allowMoreCheck(boolean original, @Local(ordinal = 0, argsOnly = true) ItemStack itemStack) {
        return original || (interestItems.contains(itemStack.getItem()) || allowedBarterItems.contains(itemStack.getItem()));
    }

    @ModifyExpressionValue(method = "canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 1))
    private boolean taiyitist$allowMoreCheck0(boolean original, @Local(ordinal = 1, argsOnly = true) ItemStack itemStack) {
        return original || (interestItems.contains(itemStack.getItem()) || allowedBarterItems.contains(itemStack.getItem()));
    }

    @Override
    public Set<Item> bridge$allowedBarterItems() {
        return allowedBarterItems;
    }

    @Override
    public Set<Item> bridge$interestItems() {
        return interestItems;
    }
}
