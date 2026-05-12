package org.teneted.taiyitist.mixin.world.item.trading;

import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.trading.InjectionMerchantOffer;

@Mixin(MerchantOffer.class)
public class MixinMerchantOffer implements InjectionMerchantOffer {
}
