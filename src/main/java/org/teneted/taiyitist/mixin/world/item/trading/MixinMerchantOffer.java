package org.teneted.taiyitist.mixin.world.item.trading;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.CreateConstructor;
import org.teneted.taiyitist.asm.annotation.ShadowConstructor;
import org.teneted.taiyitist.injection.world.item.trading.InjectionMerchantOffer;

import java.util.Optional;

@Mixin(MerchantOffer.class)
public class MixinMerchantOffer implements InjectionMerchantOffer {

    private CraftMerchantRecipe bukkitHandle;

    @ShadowConstructor
    public void taiyitist$constructor(final ItemCost baseCostA, final Optional<ItemCost> costB, final ItemStack result, final int uses, final int maxUses, final int xp, final float priceMultiplier, final int demand) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(final ItemCost baseCostA, final Optional<ItemCost> costB, final ItemStack result, final int uses, final int maxUses, final int xp, final float priceMultiplier, final int demand, CraftMerchantRecipe bukkit) {
        taiyitist$constructor(baseCostA, costB, result, uses, maxUses, xp, priceMultiplier, demand);
        this.bukkitHandle = bukkit;
    }

    @Override
    public CraftMerchantRecipe asBukkit() {
        return (bukkitHandle == null) ? bukkitHandle = new CraftMerchantRecipe(((MerchantOffer) (Object) this)) : bukkitHandle;
    }

    @Override
    public void taiyitist$setCraftMerchantRecipe(CraftMerchantRecipe bukkit) {
        this.bukkitHandle = bukkit;
    }
}