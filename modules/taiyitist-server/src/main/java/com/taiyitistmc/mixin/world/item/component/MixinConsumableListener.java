package com.taiyitistmc.mixin.world.item.component;

import com.taiyitistmc.injection.world.item.component.InjectionCosumableListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ConsumableListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ConsumableListener.class)
public interface MixinConsumableListener extends InjectionCosumableListener {

    @Override
    default void cancelUsingItem(ServerPlayer entityplayer, ItemStack itemstack) {

    }
}
