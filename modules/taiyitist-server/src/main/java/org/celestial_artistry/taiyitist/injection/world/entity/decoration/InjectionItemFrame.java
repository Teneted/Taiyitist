package org.celestial_artistry.taiyitist.injection.world.entity.decoration;

import net.minecraft.world.item.ItemStack;

public interface InjectionItemFrame {

    default void setItem(ItemStack itemstack, boolean flag, boolean playSound) {
    }
}
