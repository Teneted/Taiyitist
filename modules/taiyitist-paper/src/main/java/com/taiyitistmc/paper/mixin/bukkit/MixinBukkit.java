package com.taiyitistmc.paper.mixin.bukkit;

import com.com.taiyitistmc.paper.addon.ServerAddon;
import com.taiyitistmc.asm.annotation.TransformAccess;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Bukkit.class, remap = false)
public class MixinBukkit {

    @Shadow private static Server server;

    @NotNull
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static String getMinecraftVersion() {
        return ((ServerAddon)server).getMinecraftVersion();
    }
}
