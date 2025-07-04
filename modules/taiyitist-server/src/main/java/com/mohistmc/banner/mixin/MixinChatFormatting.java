package com.mohistmc.banner.mixin;

import com.mohistmc.banner.asm.annotation.TransformAccess;
import net.minecraft.ChatFormatting;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatFormatting.class)
public abstract class MixinChatFormatting {

    @Shadow
    public static ChatFormatting[] values() {
        return null;
    }

    // Paper start - add method to get by hex value
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static ChatFormatting getByHexValue(int i) {
        for (ChatFormatting value : values()) {
            if (value.getColor() != null && value.getColor() == i) {
                return value;
            }
        }

        return null;
    }
    // Paper end - add method to get by hex value
}
