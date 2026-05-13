package org.teneted.taiyitist.injection.world.item.trading;

import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;

public interface InjectionMerchantOffer {

    default CraftMerchantRecipe asBukkit() {
        throw new AssertionError("Not implemented");
    }

    default void taiyitist$setCraftMerchantRecipe(CraftMerchantRecipe bukkit) {
        throw new AssertionError("Not implemented");
    }
}
