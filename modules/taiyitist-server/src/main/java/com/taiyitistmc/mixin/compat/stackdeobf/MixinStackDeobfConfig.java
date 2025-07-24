package com.taiyitistmc.mixin.compat.stackdeobf;

import com.llamalad7.mixinextras.sugar.Local;
import dev.booky.stackdeobf.config.StackDeobfConfig;
import dev.booky.stackdeobf.mappings.providers.AbstractMappingProvider;
import dev.booky.stackdeobf.mappings.providers.MojangMappingProvider;
import dev.booky.stackdeobf.util.VersionData;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;


@Mixin(value = StackDeobfConfig.class, remap = false)
public class MixinStackDeobfConfig {


    @Shadow private AbstractMappingProvider mappingProvider;

    @Redirect(method = "load", at = @At(value = "FIELD", target = "Ldev/booky/stackdeobf/config/StackDeobfConfig;mappingProvider:Ldev/booky/stackdeobf/mappings/providers/AbstractMappingProvider;"))
    private static void taiyitist$useMojang(StackDeobfConfig instance, AbstractMappingProvider value, @Local(argsOnly = true) VersionData versionData) {
        EnvType env = FabricLoader.getInstance().getEnvironmentType();
        String envName = env.name().toLowerCase(Locale.ROOT);
        ((MixinStackDeobfConfig) (Object) instance).setMappingProvider(new MojangMappingProvider(versionData, envName));
    }

    private void setMappingProvider(AbstractMappingProvider value) {
        this.mappingProvider = value;
    }
}
