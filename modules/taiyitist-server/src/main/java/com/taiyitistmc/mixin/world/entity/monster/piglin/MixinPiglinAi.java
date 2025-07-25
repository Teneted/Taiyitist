package com.taiyitistmc.mixin.world.entity.monster.piglin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(PiglinAi.class)
public abstract class MixinPiglinAi {

    @Shadow
    private static void stopWalking(Piglin piglin) {
    }

    @Shadow
    private static ItemStack removeOneItemFromItemEntity(ItemEntity itemEntity) {
        return null;
    }

    @Shadow
    protected static boolean isLovedItem(ItemStack itemStack) {
        return false;
    }

    @Shadow
    private static void holdInOffhand(ServerLevel serverLevel, Piglin piglin, ItemStack itemStack) {
    }

    @Shadow
    private static void admireGoldItem(LivingEntity livingEntity) {
    }

    @Shadow
    private static boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Shadow
    private static boolean hasEatenRecently(Piglin piglin) {
        return false;
    }

    @Shadow
    private static void eat(Piglin piglin) {
    }

    @Shadow
    private static void putInInventory(Piglin piglin, ItemStack itemStack) {
    }

    @Shadow
    protected static boolean isBarterCurrency(ItemStack itemStack) {
        return false;
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    protected static void pickUpItem(ServerLevel serverLevel, Piglin piglin, ItemEntity itemEntity) {
        stopWalking(piglin);
        ItemStack itemStack;
        if (itemEntity.getItem().is(Items.GOLD_NUGGET) && !CraftEventFactory.callEntityPickupItemEvent(piglin, itemEntity, 0, false).isCancelled()) {
            piglin.take(itemEntity, itemEntity.getItem().getCount());
            itemStack = itemEntity.getItem();
            itemEntity.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
            itemEntity.discard();
        } else if (!CraftEventFactory.callEntityPickupItemEvent(piglin, itemEntity, itemEntity.getItem().getCount() - 1, false).isCancelled()) {
            piglin.take(itemEntity, 1);
            itemStack = removeOneItemFromItemEntity(itemEntity);
        } else {
            return;
        }
        // CraftBukkit end

        if (isLovedItem(itemStack)) {
            piglin.getBrain().eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            holdInOffhand(serverLevel, piglin, itemStack);
            admireGoldItem(piglin);
        } else if (isFood(itemStack) && !hasEatenRecently(piglin)) {
            eat(piglin);
        } else {
            boolean bl = !piglin.equipItemIfPossible(serverLevel, itemStack).equals(ItemStack.EMPTY);
            if (!bl) {
                putInInventory(piglin, itemStack);
            }
        }
    }

    // CraftBukkit start - Added method to allow checking for custom payment items
    private static boolean isLovedItem(ItemStack itemstack, Piglin piglin) {
        return isLovedItem(itemstack) || (piglin.bridge$interestItems().contains(itemstack.getItem()) || piglin.bridge$allowedBarterItems().contains(itemstack.getItem()));
    }
    // CraftBukkit end

    @Unique
    private static AtomicReference<Piglin> taiyitist$piglin = new AtomicReference<>();

    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin(ServerLevel serverLevel, Piglin piglin, ItemEntity itemEntity, CallbackInfo ci) {
        taiyitist$piglin.set(piglin);
    }

    @Inject(method = "stopHoldingOffHandItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin0(ServerLevel serverLevel, Piglin piglin, boolean bl, CallbackInfo ci) {
        taiyitist$piglin.set(piglin);
    }

    @Inject(method = "isNotHoldingLovedItemInOffHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin1(Piglin piglin, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$piglin.set(piglin);
    }

    @Inject(method = "wantsToPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin2(Piglin piglin, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$piglin.set(piglin);
    }

    @Inject(method = "stopHoldingOffHandItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isBarterCurrency(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin4(ServerLevel serverLevel, Piglin piglin, boolean bl, CallbackInfo ci) {
        taiyitist$piglin.set(piglin);
    }

    @Inject(method = "wantsToPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isBarterCurrency(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin3(Piglin piglin, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$piglin.set(piglin);
    }

    @Inject(method = "canAdmire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isBarterCurrency(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void taiyitist$addPigin5(Piglin piglin, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$piglin.set(piglin);
    }

    @ModifyReturnValue(method = "isLovedItem", at = @At("RETURN"))
    private static boolean taiyitist$allowCheck(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
        return original || taiyitist$piglin.get() != null ? (taiyitist$piglin.get().bridge$interestItems().contains(itemStack.getItem()) || taiyitist$piglin.get().bridge$allowedBarterItems().contains(itemStack.getItem())) : original;
    }

    // CraftBukkit start - Changes to allow custom payment for bartering
    private static boolean isBarterCurrency(ItemStack itemstack, Piglin piglin) {
        return isBarterCurrency(itemstack) || piglin.bridge$allowedBarterItems().contains(itemstack.getItem());
    }
    // CraftBukkit end

    @ModifyReturnValue(method = "isBarterCurrency", at = @At("RETURN"))
    private static boolean taiyitist$isBarterCurrency(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
        return original || taiyitist$piglin.get() != null ? taiyitist$piglin.get().bridge$allowedBarterItems().contains(itemStack.getItem()) : original;
    }
}
