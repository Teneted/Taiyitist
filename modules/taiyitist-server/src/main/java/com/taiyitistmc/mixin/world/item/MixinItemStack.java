package com.taiyitistmc.mixin.world.item;

import com.taiyitistmc.injection.world.item.InjectionItemStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public class MixinItemStack implements InjectionItemStack {

    @Mutable
    @Shadow
    @Deprecated @Nullable private Item item;

    @Shadow @Final private PatchedDataComponentMap components;

    // CraftBukkit start
    @Deprecated
    public void setItem(Item item) {
        this.item = item;
    }
    // CraftBukkit end

    @Override
    public void restorePatch(DataComponentPatch datacomponentpatch) {
        this.components.restorePatch(datacomponentpatch);
    }
}
