package com.taiyitistmc.mixin.nbt;

import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompoundTag.class)
public abstract class MixinCompoundTag {

    @Shadow public abstract boolean contains(String string, int i);

    @Shadow @Final private Map<String, Tag> tags;

    @Shadow public abstract long getLong(String string);

    @Inject(method = "putUUID", at = @At("HEAD"))
    private void banner$SupportOldUUID(String key, UUID uUID, CallbackInfo ci) {
        // Paper start - Support old UUID format
        if (this.contains(key + "Most", net.minecraft.nbt.Tag.TAG_ANY_NUMERIC) && this.contains(key + "Least", net.minecraft.nbt.Tag.TAG_ANY_NUMERIC)) {
            this.tags.remove(key + "Most");
            this.tags.remove(key + "Least");
        }
        // Paper end - Support old UUID format
    }

    @Inject(method = "getUUID", at = @At("HEAD"), cancellable = true)
    private void banner$SupportOldUUID0(String key, CallbackInfoReturnable<UUID> cir) {
        // Paper start - Support old UUID format
        if (!contains(key, 11) && this.contains(key + "Most", net.minecraft.nbt.Tag.TAG_ANY_NUMERIC) && this.contains(key + "Least", net.minecraft.nbt.Tag.TAG_ANY_NUMERIC)) {
            cir.setReturnValue(new UUID(this.getLong(key + "Most"), this.getLong(key + "Least")));
        }
        // Paper end - Support old UUID format
    }

    @Inject(method = "hasUUID", at = @At("HEAD"), cancellable = true)
    private void banner$SupportOldUUID1(String key, CallbackInfoReturnable<Boolean> cir) {
        // Paper start - Support old UUID format
        if (this.contains(key + "Most", net.minecraft.nbt.Tag.TAG_ANY_NUMERIC) && this.contains(key + "Least", net.minecraft.nbt.Tag.TAG_ANY_NUMERIC)) {
            cir.setReturnValue(true);
        }
        // Paper end - Support old UUID format
    }
}
