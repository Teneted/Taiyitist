package org.teneted.taiyitist.mixin.world.inventory;

import net.minecraft.world.inventory.TransientCraftingContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.inventory.InjectionTransientCraftingContainer;

@Mixin(TransientCraftingContainer.class)
public class MixinTransientCraftingContainer implements InjectionTransientCraftingContainer {
}
