package org.teneted.taiyitist.mixin.world.item.trading;

import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.trading.InjectionMerchant;

@Mixin(Merchant.class)
public class MixinMerchant implements InjectionMerchant {
}
