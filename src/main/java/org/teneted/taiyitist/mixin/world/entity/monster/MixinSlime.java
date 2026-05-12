package org.teneted.taiyitist.mixin.world.entity.monster;

import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.monster.InjectionSlime;

@Mixin(Slime.class)
public class MixinSlime implements InjectionSlime {
}
