package com.taiyitistmc.mixin.world.item.component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class MixinConsumable {

    @Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void taiyitist$pushEffectCause(Level level, LivingEntity livingEntity, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        // CraftBukkit start
        EntityPotionEffectEvent.Cause cause;
        if (itemStack.is(Items.MILK_BUCKET)) {
            livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.MILK);
        } else if (itemStack.is(Items.POTION)) {
            livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.POTION_DRINK);
        } else {
            livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.FOOD);
        }

    }

    // CraftBukkit start
    public void cancelUsingItem(net.minecraft.server.level.ServerPlayer entityplayer, ItemStack itemstack) {
        itemstack.getAllOfType(ConsumableListener.class).forEach((consumablelistener) -> {
            consumablelistener.cancelUsingItem(entityplayer, itemstack);
        });
        entityplayer.server.getPlayerList().sendActivePlayerEffects(entityplayer);
    }
    // CraftBukkit end
}
