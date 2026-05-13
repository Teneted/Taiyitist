package org.teneted.taiyitist.injection.world.level.block.entity;

import net.minecraft.world.item.ItemStack;

public interface InjectionJukeboxBlockEntity {

    default void setSongItemWithoutPlaying(final ItemStack itemStack, long ticks) {
        throw new AssertionError("Not implemented");
    }
}
