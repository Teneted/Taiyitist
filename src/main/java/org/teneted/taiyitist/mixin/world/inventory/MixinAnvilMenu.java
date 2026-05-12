package org.teneted.taiyitist.mixin.world.inventory;

import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.inventory.InjectionAnvilMenu;

@Mixin(AnvilMenu.class)
public class MixinAnvilMenu implements InjectionAnvilMenu {
}
