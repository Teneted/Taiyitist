package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionAgeableMob;

@Mixin(AgeableMob.class)
public class MixinAgeableMob implements InjectionAgeableMob {
}
