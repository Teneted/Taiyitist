package com.taiyitistmc.paper.mixin.bukkit;

import com.com.taiyitistmc.paper.addon.ServerAddon;
import org.bukkit.Server;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Server.class, remap = false)
public interface MixinServer extends ServerAddon {
}
