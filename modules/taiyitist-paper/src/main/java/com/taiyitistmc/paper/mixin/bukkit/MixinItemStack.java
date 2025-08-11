package com.taiyitistmc.paper.mixin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemStack.class, remap = false)
public abstract class MixinItemStack {

    public @NotNull String translationKey() {
        return Bukkit.getUnsafe().getTranslationKey(((ItemStack) (Object) this));
    }
}
