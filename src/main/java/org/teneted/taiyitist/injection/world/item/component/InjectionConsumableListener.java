package org.teneted.taiyitist.injection.world.item.component;

import net.minecraft.world.item.ItemStack;

public interface InjectionConsumableListener {

    default void cancelUsingItem(net.minecraft.server.level.ServerPlayer entityplayer, ItemStack itemstack) {} // CraftBukkit

}
