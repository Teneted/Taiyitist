package org.teneted.taiyitist.mixin.world.level.portal;

import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.portal.InjectionPortalForcer;

@Mixin(PortalForcer.class)
public class MixinPortalForcer implements InjectionPortalForcer {
}
