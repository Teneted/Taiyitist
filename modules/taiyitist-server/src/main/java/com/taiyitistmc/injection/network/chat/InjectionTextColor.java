package com.taiyitistmc.injection.network.chat;

import net.minecraft.ChatFormatting;

public interface InjectionTextColor {

    default ChatFormatting bridge$format() {
        return null;
    }

    default void taiyitist$setFormat(ChatFormatting format) {
    }
}
