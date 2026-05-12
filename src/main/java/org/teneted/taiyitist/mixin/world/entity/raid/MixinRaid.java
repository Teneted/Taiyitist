package org.teneted.taiyitist.mixin.world.entity.raid;

import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.raid.InjectionRaid;

@Mixin(Raid.class)
public class MixinRaid implements InjectionRaid {
}
