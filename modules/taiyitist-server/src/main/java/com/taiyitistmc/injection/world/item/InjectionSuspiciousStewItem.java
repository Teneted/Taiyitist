package com.taiyitistmc.injection.world.item;

import net.minecraft.world.item.ItemStack;

public interface InjectionSuspiciousStewItem {

    default void cancelUsingItem(net.minecraft.server.level.ServerPlayer entityplayer, ItemStack itemstack) {

    }
}
