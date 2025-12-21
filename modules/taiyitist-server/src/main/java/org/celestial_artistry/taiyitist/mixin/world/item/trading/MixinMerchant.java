package org.celestial_artistry.taiyitist.mixin.world.item.trading;

import org.celestial_artistry.taiyitist.injection.world.item.trading.InjectionMerchant;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMerchant;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Merchant.class)
public interface MixinMerchant extends InjectionMerchant {

    @Override
    CraftMerchant getCraftMerchant();
}
