package org.teneted.taiyitist.mixin.network.chat;

import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.chat.InjectionTextColor;

@Mixin(TextColor.class)
public class MixinTextColor implements InjectionTextColor {
}
