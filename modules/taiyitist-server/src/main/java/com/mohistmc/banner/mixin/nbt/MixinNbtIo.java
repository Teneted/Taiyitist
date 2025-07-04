package com.mohistmc.banner.mixin.nbt;

import java.io.DataInput;
import java.io.DataInputStream;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtIo.class)
public class MixinNbtIo {

    @Inject(method = "readAnyTag", at = @At("HEAD"))
    private static void banner$handleInput(DataInput dataInput, NbtAccounter nbtAccounter, CallbackInfoReturnable<Tag> cir) {
        // Spigot start
        if (dataInput instanceof io.netty.buffer.ByteBufInputStream byteBufInputStream) {
            dataInput = new DataInputStream(new org.spigotmc.LimitStream(byteBufInputStream, nbtAccounter));
        }
        // Spigot end
    }
}
