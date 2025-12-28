package org.teneted.taiyitist.injection.world.food;

import net.minecraft.world.entity.player.Player;

public interface InjectionFoodData {

    default int bridge$saturatedRegenRate() {
        return 0;
    }

    default void taiyitist$setSaturatedRegenRate(int saturatedRegenRate) {
    }

    default int bridge$unsaturatedRegenRate() {
        return 0;
    }

    default void taiyitist$setUnsaturatedRegenRate(int unsaturatedRegenRate) {
    }

    default int bridge$starvationRate() {
        return 0;
    }

    default void taiyitist$setStarvationRate(int starvationRate) {
    }

    default Player getEntityhuman() {
        return null;
    }

    default void setEntityhuman(Player entityhuman) {
    }
}
