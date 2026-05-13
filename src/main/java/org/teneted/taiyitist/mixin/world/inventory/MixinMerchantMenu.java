package org.teneted.taiyitist.mixin.world.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantMenu;
import org.bukkit.craftbukkit.inventory.view.CraftMerchantView;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.inventory.InjectionMerchantMenu;

@Mixin(MerchantMenu.class)
public abstract class MixinMerchantMenu extends AbstractContainerMenu implements InjectionMerchantMenu {

    protected MixinMerchantMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public CraftMerchantView getBukkitView() {
        return (CraftMerchantView) super.getBukkitView();
    }
}
