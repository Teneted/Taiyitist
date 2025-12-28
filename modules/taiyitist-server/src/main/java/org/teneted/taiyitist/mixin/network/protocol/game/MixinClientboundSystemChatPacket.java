package org.teneted.taiyitist.mixin.network.protocol.game;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSystemChatPacket.class)
public class MixinClientboundSystemChatPacket {

    @Unique
    public void taiyitist$constructor(Component content, boolean overlay) {
        throw new RuntimeException();
    }

    @Unique
    public void taiyitist$constructor(String content, boolean overlay) {
        taiyitist$constructor(Component.Serializer.fromJson(content), overlay);
    }

    @Unique
    public void taiyitist$constructor(BaseComponent[] content, boolean overlay) {
        taiyitist$constructor(ComponentSerializer.toString(content), overlay);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/chat/Component;Z)V", at = @At("RETURN"))
    private void arclight$init(Component content, boolean overlay, CallbackInfo ci) {
        this.a = Component.Serializer.toJson(content);
    }

    @Unique
    private String a;

    @Unique
    public String content0() {
        return a;
    }

}
