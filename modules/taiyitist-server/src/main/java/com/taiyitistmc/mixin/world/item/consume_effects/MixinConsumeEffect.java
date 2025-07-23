package com.taiyitistmc.mixin.world.item.consume_effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConsumeEffect.class)
public interface MixinConsumeEffect {

    @Shadow boolean apply(Level level, ItemStack itemStack, net.minecraft.world.entity.LivingEntity livingEntity);

    // CraftBukkit start
    default boolean apply(Level world, ItemStack itemstack, LivingEntity entityliving, EntityPotionEffectEvent.Cause cause) {
        entityliving.pushEffectCause(EntityPotionEffectEvent.Cause.UNKNOWN);
        return this.apply(world, itemstack, entityliving);
    }
    // CraftBukkit end
}
