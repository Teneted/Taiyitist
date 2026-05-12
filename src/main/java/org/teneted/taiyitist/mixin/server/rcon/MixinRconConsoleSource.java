package org.teneted.taiyitist.mixin.server.rcon;

import net.minecraft.server.rcon.RconConsoleSource;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.rcon.InjectionRconConsoleSource;

@Mixin(RconConsoleSource.class)
public class MixinRconConsoleSource implements InjectionRconConsoleSource {
}
