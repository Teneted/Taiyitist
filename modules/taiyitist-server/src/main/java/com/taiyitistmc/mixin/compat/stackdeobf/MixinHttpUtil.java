package com.taiyitistmc.mixin.compat.stackdeobf;

import com.taiyitistmc.util.I18n;
import dev.booky.stackdeobf.http.HttpUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = HttpUtil.class, remap = false)
public abstract class MixinHttpUtil {

    @ModifyArg(method = "lambda$getAsync$2", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;log(Lorg/apache/logging/log4j/Level;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"), index = 1)
    private static String taiyitist$i18n(String message) {
        return I18n.as("stackdeobf.received");
    }
}
