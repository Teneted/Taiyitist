package org.celestial_artistry.taiyitist.injection.network.chat;

import net.minecraft.ChatFormatting;

public interface InjectionTextColor {

    default ChatFormatting bridge$format() {
        return null;
    }

    default void taiyitist$setFormat(ChatFormatting format) {
    }
}
