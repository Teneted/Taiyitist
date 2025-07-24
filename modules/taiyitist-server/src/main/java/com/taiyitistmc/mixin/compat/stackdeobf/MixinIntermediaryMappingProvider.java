package com.taiyitistmc.mixin.compat.stackdeobf;

import com.taiyitistmc.util.I18n;
import dev.booky.stackdeobf.mappings.providers.IntermediaryMappingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = IntermediaryMappingProvider.class, remap = false)
public class MixinIntermediaryMappingProvider {

    @ModifyArg(method = "lambda$downloadMappings0$0", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"), index = 0)
    private static String taiyitist$resetI18n(String message) {
        return I18n.as("stackdeobf.downloading.intermediary");
    }
}
