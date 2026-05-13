package org.teneted.taiyitist.injection.world.inventory;

import org.bukkit.craftbukkit.inventory.view.CraftMerchantView;

public interface InjectionMerchantMenu extends InjectionAbstractContainerMenu {

    @Override
    default CraftMerchantView getBukkitView() {
        throw new IllegalStateException("Not implemented");
    }
}
