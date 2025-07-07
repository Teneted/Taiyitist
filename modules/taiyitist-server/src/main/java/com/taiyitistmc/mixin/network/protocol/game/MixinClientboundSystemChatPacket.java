package com.taiyitistmc.mixin.network.protocol.game;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientboundSystemChatPacket.class)
public class MixinClientboundSystemChatPacket {

    private String a;

    @ShadowConstructor
    public void taiyitist$constructor(Component content, boolean overlay) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(BaseComponent[] content, boolean overlay) {
        taiyitist$constructor(CraftChatMessage.fromJSON(ComponentSerializer.toString(content)), overlay);
    }

    public String content0() {
        return a;
    }

}
