package com.mohistmc.banner.mixin.interaction;

import com.mohistmc.banner.asm.annotation.TransformAccess;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.ListTag;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Rotations.class)
public class MixinRotations {

    private Void dummy_var;

    @Inject(method = "<init>(Lnet/minecraft/nbt/ListTag;)V", at = @At("RETURN"))
    private void banner$init(ListTag listTag, CallbackInfo ci) {
        dummy_var = null;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static Rotations createWithoutValidityChecks(float x, float y, float z) {
        var rotations = new Rotations(x, y, z);
        (((MixinRotations) (Object) rotations)).setDummy_var(null);
        return rotations;
    }

    public Void getDummy_var() {
        return dummy_var;
    }

    public void setDummy_var(Void dummy_var) {
        this.dummy_var = dummy_var;
    }
}
