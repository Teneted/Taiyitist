package org.teneted.taiyitist.mixin.world.entity.ai.gossip;

import net.minecraft.world.entity.ai.gossip.GossipContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.ai.gossip.InjectionGossipContainer;

@Mixin(GossipContainer.class)
public class MixinGossipContainer implements InjectionGossipContainer {
}
