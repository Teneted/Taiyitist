package org.teneted.taiyitist.mixin.world.entity.decoration;

import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.decoration.InjectionItemFrame;

@Mixin(ItemFrame.class)
public class MixinItemFrame implements InjectionItemFrame {
}
