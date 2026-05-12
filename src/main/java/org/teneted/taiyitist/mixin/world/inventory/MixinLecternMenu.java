package org.teneted.taiyitist.mixin.world.inventory;

import net.minecraft.world.inventory.LecternMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.inventory.InjectionLecternMenu;

@Mixin(LecternMenu.class)
public class MixinLecternMenu implements InjectionLecternMenu {
}
