package org.teneted.taiyitist.mixin.network.chat;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.chat.InjectionComponent;

@Mixin(Component.class)
public interface MixinComponent extends InjectionComponent {
}
