package org.teneted.taiyitist.mixin.world.item.component;

import net.minecraft.world.item.component.ConsumableListener;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.item.component.InjectionConsumableListener;

@Mixin(ConsumableListener.class)
public interface MixinConsumableListener extends InjectionConsumableListener {
}
