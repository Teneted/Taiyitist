package com.taiyitistmc.translate.mixin;

import com.taiyitistmc.TaiyitistMCStart;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoggerChunkProgressListener.class)
public abstract class MixinLoggerChunkProgressListener {

    @ModifyConstant(method = "stop", constant = @Constant(stringValue = "Time elapsed: {} ms"))
    private String bosom$i18nTime(String constant) {
        return TaiyitistMCStart.I18N.as("world.time.elapsed");
    }
}