package com.taiyitistmc.bukkit;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;

public class QueryAnswerPayload implements CustomQueryAnswerPayload {

    public final FriendlyByteBuf buffer;

    public QueryAnswerPayload(final net.minecraft.network.FriendlyByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBytes(this.buffer.copy());
    }
}
