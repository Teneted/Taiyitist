package org.teneted.taiyitist.mixin.world.entity.monster.piglin;

import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.monster.piglin.InjectionPiglin;

@Mixin(Piglin.class)
public class MixinPiglin implements InjectionPiglin {
}
