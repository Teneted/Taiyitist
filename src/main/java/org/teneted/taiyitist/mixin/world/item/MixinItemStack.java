package org.teneted.taiyitist.mixin.world.item;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.InjectionItemStack;

@Mixin(ItemStack.class)
public class MixinItemStack implements InjectionItemStack {
}
