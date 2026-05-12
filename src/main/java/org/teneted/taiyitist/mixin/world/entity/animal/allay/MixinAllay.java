package org.teneted.taiyitist.mixin.world.entity.animal.allay;

import net.minecraft.world.entity.animal.allay.Allay;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.animal.allay.InjectionAllay;

@Mixin(Allay.class)
public class MixinAllay implements InjectionAllay {
}
