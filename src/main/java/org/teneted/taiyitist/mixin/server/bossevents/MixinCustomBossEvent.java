package org.teneted.taiyitist.mixin.server.bossevents;

import net.minecraft.server.bossevents.CustomBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.bossevents.InjectionCustomBossEvent;

@Mixin(CustomBossEvent.class)
public class MixinCustomBossEvent implements InjectionCustomBossEvent {
}
