package org.teneted.taiyitist.mixin.server.network;

import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.network.InjectionServerCommonPacketListenerImpl;

@Mixin(ServerCommonPacketListenerImpl.class)
public class MixinServerCommonPacketListenerImpl implements InjectionServerCommonPacketListenerImpl {
}
