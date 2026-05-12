package org.teneted.taiyitist.mixin.world.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.inventory.InjectionAbstractContainerMenu;

@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu implements InjectionAbstractContainerMenu {
}
