package com.taiyitistmc.mixin;

import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrashReportCategory.class)
public class MixinCrashReportCategory {

    @Shadow private StackTraceElement[] stackTrace;

    @Inject(method = "fillInStackTrace", at = @At("RETURN"))
    private void banner$deobf(int i, CallbackInfoReturnable<Integer> cir) {
        this.stackTrace = io.papermc.paper.util.StacktraceDeobfuscator.INSTANCE.deobfuscateStacktrace(this.stackTrace); // Paper
    }
}
