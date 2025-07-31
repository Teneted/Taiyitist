package com.taiyitistmc.mixin.network.protocol.login;

import com.taiyitistmc.bukkit.QueryAnswerPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerboundCustomQueryAnswerPacket.class)
public abstract class MixinServerboundCustomQueryAnswerPacket {

    @Shadow
    private static CustomQueryAnswerPayload readUnknownPayload(FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Shadow @Final private static int MAX_PAYLOAD_SIZE;

    /**
     * @author wdog5
     * @reason velocity
     */
    @Overwrite
    private static CustomQueryAnswerPayload readPayload(int queryId, FriendlyByteBuf buf) {
        // Paper start - MC Utils - default query payloads
        FriendlyByteBuf buffer = buf.readNullable((buf2) -> {
            int i = buf2.readableBytes();
            if (i >= 0 && i <= MAX_PAYLOAD_SIZE) {
                return new FriendlyByteBuf(buf2.readBytes(i));
            } else {
                throw new IllegalArgumentException("Payload may not be larger than " + MAX_PAYLOAD_SIZE + " bytes");
            }
        });
        return buffer == null ? null : new QueryAnswerPayload(buffer);
        // Paper end - MC Utils - default query payloads
    }

}
