package com.taiyitistmc.mixin.compat.stackdeobf;

import com.taiyitistmc.config.TaiyitistConfigUtil;
import com.taiyitistmc.util.I18n;
import dev.booky.stackdeobf.mappings.CachedMappings;
import dev.booky.stackdeobf.mappings.providers.AbstractMappingProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mixin(value = CachedMappings.class, remap = false)
public class MixinCachedMappings {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow @Final private Int2ObjectMap<String> classes;

    @Redirect(method = "create(Ljava/nio/file/Path;Ldev/booky/stackdeobf/mappings/providers/AbstractMappingProvider;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"))
    private static void taiyitist$resetInfo(Logger instance, String string) {
        LOGGER.info(I18n.as("stackdeobf.creating"));
    }

    @ModifyArg(method = "lambda$create$1", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"), index = 0)
    private static String taiyitist$resetInfo0(String message) {
        return I18n.as("stackdeobf.cached.mappings");
    }

    @Inject(method = "create(Ljava/nio/file/Path;Ldev/booky/stackdeobf/mappings/providers/AbstractMappingProvider;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private static void taiyitist$configure(Path cacheDir, AbstractMappingProvider provider, CallbackInfoReturnable<CompletableFuture<CachedMappings>> cir) {
        if (!TaiyitistConfigUtil.stackdeobf()) {
            cir.setReturnValue(null);
        }
        return;
    }
}