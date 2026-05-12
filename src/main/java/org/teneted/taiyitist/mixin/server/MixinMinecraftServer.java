package org.teneted.taiyitist.mixin.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.InjectionMinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements InjectionMinecraftServer {
}
