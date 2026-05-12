package org.teneted.taiyitist.mixin.world.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.entity.item.PrimedTnt;
import org.teneted.taiyitist.injection.world.entity.item.InjectionPrimedTnt;

@Mixin(PrimedTnt.class)
public class MixinPrimedTnt implements InjectionPrimedTnt {
}
