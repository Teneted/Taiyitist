package org.teneted.taiyitist.mixin.world.level.dimension.end;

import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.dimension.end.InjectionEndDragonFight;

@Mixin(EnderDragonFight.class)
public class MixinEndDragonFight implements InjectionEndDragonFight {
}
