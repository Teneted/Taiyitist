package com.taiyitistmc.injection.world.inventory;

import net.minecraft.world.entity.player.Inventory;

public interface InjectionLecternMenu {

    default void bridge$setPlayerInventory(Inventory playerInventory) {
    }
}
