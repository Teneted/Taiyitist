package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionServerLevel;

@Mixin(ServerLevel.class)
public class MixinSeverLevel implements InjectionServerLevel {
}
