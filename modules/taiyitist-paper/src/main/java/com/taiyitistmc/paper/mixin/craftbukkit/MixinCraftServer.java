package com.taiyitistmc.paper.mixin.craftbukkit;

import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftServer.class)
public class MixinCraftServer {

    @Shadow @Final protected DedicatedServer console;

    public String getMinecraftVersion() {
        return this.console.getServerVersion();
    }
}
