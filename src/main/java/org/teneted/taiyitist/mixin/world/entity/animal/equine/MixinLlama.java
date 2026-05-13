package org.teneted.taiyitist.mixin.world.entity.animal.equine;

import net.minecraft.world.entity.animal.equine.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.animal.equine.InjectionLlama;

@Mixin(Llama.class)
public class MixinLlama implements InjectionLlama {
}
