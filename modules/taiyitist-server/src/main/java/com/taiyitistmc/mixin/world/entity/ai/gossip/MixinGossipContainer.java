package com.taiyitistmc.mixin.world.entity.ai.gossip;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;

// TODO fix inject
@Mixin(GossipContainer.class)
public class MixinGossipContainer {

    // CraftBukkit start - store reference to villager entity
    public Villager villager;

    @CreateConstructor
    public void taiyitist$constructor() {
        this.villager = villager;
    }

    // CraftBukkit end
}
