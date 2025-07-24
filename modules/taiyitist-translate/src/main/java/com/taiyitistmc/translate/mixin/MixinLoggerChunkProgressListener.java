package com.taiyitistmc.translate.mixin;

import com.taiyitistmc.TaiyitistMCStart;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoggerChunkProgressListener.class)
public abstract class MixinLoggerChunkProgressListener {

    @ModifyArg(method = "onStatusChange", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"), index = 0)
    private String taiyitist$prepAreaI18n(String s) {
        return TaiyitistMCStart.I18N.as("world.preparingSpawn");
    }

    @ModifyConstant(method = "stop", constant = @Constant(stringValue = "Time elapsed: {} ms"))
    private String bosom$i18nTime(String constant) {
        return TaiyitistMCStart.I18N.as("world.time.elapsed");
    }
}