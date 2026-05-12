package org.teneted.taiyitist.mixin.world.entity.monster;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.monster.InjectionCreeper;

@Mixin(Creeper.class)
public class MixinCreeper implements InjectionCreeper {
}
