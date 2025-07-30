package com.taiyitistmc.bukkit;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerInfoChannelPayload(ResourceLocation id, FriendlyByteBuf buffer) implements CustomQueryPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBytes(this.buffer.copy());
    }
}
