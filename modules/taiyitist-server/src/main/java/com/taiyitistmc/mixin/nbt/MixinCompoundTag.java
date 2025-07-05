package com.taiyitistmc.mixin.nbt;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompoundTag.class)
public class MixinCompoundTag {

    @Shadow @Final
    private Map<String, Tag> tags;

    /**
     * @author Banner
     * @reason
     */
    @Inject(method = "copy*", at = @At("HEAD"), cancellable = true)
    private void banner$safeCopy(CallbackInfoReturnable<CompoundTag> cir) {
        try {
            Map<String, Tag> map = Maps.newHashMap(Maps.transformValues(this.tags, Tag::copy));
            cir.setReturnValue(new CompoundTag(map));
        } catch (Exception e) {
            cir.setReturnValue(new CompoundTag());
        }
        cir.cancel();
    }
}
