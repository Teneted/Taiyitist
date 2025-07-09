package com.taiyitistmc.mixin.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerFunctionManager.class)
public class MixinServerFunctionManager {

    @Shadow @Final private MinecraftServer server;

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.bridge$getVanillaCommands().getDispatcher();// CraftBukkit
    }
}
