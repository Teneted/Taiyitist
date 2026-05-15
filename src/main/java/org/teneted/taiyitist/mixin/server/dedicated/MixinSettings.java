package org.teneted.taiyitist.mixin.server.dedicated;

import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Properties;

@Mixin(Settings.class)
public class MixinSettings {

    @Inject(method = "loadFromFile", at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;newInputStream(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;"), cancellable = true)
    private static void taiyitist$loadFromFile(Path file, CallbackInfoReturnable<Properties> cir) {
        // CraftBukkit start - SPIGOT-7465, MC-264979: Don't load if file doesn't exist
        if (!file.toFile().exists()) {
            cir.setReturnValue(new Properties());
        }
        // CraftBukkit end
    }

    @Inject(method = "store", at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;newBufferedWriter(Ljava/nio/file/Path;Ljava/nio/charset/Charset;[Ljava/nio/file/OpenOption;)Ljava/io/BufferedWriter;"), cancellable = true)
    private void taiyitist$store(Path output, CallbackInfo ci) {
        // CraftBukkit start - Don't attempt writing to file if it's read only
        if (output.toFile().exists() && !output.toFile().canWrite()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

}
