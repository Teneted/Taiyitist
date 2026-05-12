package org.teneted.taiyitist.mixin.network.chat;

import net.minecraft.core.component.DataComponentPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.chat.InjectionDataComponentPatch;

@Mixin(DataComponentPatch.class)
public class MixinDataComponentPatch implements InjectionDataComponentPatch {
}
