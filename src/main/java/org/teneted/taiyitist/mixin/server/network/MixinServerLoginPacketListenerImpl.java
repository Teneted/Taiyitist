package org.teneted.taiyitist.mixin.server.network;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.network.InjectionServerLoginPacketListenerImpl;

@Mixin(ServerLoginPacketListenerImpl.class)
public class MixinServerLoginPacketListenerImpl implements InjectionServerLoginPacketListenerImpl {
}
