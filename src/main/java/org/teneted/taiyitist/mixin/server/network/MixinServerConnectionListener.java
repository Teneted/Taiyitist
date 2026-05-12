package org.teneted.taiyitist.mixin.server.network;

import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.network.InjectionServerConnectionListener;

@Mixin(ServerConnectionListener.class)
public class MixinServerConnectionListener implements InjectionServerConnectionListener {
}
