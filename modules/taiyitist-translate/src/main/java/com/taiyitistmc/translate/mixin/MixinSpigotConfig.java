package com.taiyitistmc.translate.mixin;

import com.taiyitistmc.TaiyitistMCStart;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SpigotConfig.class, remap = false)
public class MixinSpigotConfig {

    @ModifyArg(method = "playerSample", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"), index = 0)
    private static String bosom$playerSample(String x) {
        return TaiyitistMCStart.I18N.as("spigotconfig.11");
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lorg/bukkit/configuration/file/YamlConfigurationOptions;header(Ljava/lang/String;)Lorg/bukkit/configuration/file/YamlConfigurationOptions;"), index = 0)
    private static String bosom$resetHeader(@Nullable String value) {
        return TaiyitistMCStart.I18N.as("spigotconfig.1") + "\n"
                + TaiyitistMCStart.I18N.as("spigotconfig.2") + "\n"
                + TaiyitistMCStart.I18N.as("spigotconfig.3") + "\n"
                + TaiyitistMCStart.I18N.as("spigotconfig.4") + "\n"
                + "http://www.spigotmc.org/wiki/spigot-configuration/\n"
                + "\n"
                + TaiyitistMCStart.I18N.as("spigotconfig.5") + "\n"
                + TaiyitistMCStart.I18N.as("spigotconfig.6") + "\n"
                + "\n"
                + "Discord: https://www.spigotmc.org/go/discord\n"
                + "Forums: http://www.spigotmc.org/\n";
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V"), index = 1)
    private static String bosom$resetCannotLoad(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.7");
    }

    @ModifyArg(method = "nettyThreads", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V"), index = 1)
    private static String bosom$nettyThreads(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.8");
    }

    @ModifyArg(method = "stats", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;)V", ordinal = 0), index = 1)
    private static String bosom$stats0(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.9");
    }

    @ModifyArg(method = "stats", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;)V", ordinal = 1), index = 1)
    private static String bosom$stats1(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.10");
    }

    @ModifyArg(method = "debug", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private static String bosom$debug0(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.12");
    }

    @ModifyArg(method = "debug", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;info(Ljava/lang/String;)V", ordinal = 1), index = 0)
    private static String bosom$debug1(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.13");
    }

    @ModifyArg(method = "watchdog", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotConfig;getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 1), index = 1)
    private static String bosom$watchdog(String path) {
        return TaiyitistMCStart.I18N.as("spigotconfig.15");
    }

    @ModifyArg(method = "messages", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotConfig;getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 0), index = 1)
    private static String bosom$messages0(String path) {
        return TaiyitistMCStart.I18N.as("spigotconfig.16");
    }

    @ModifyArg(method = "messages", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotConfig;getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 1), index = 1)
    private static String bosom$messages1(String path) {
        return TaiyitistMCStart.I18N.as("spigotconfig.17");
    }

    @ModifyArg(method = "messages", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotConfig;getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 2), index = 1)
    private static String bosom$messages2(String path) {
        return TaiyitistMCStart.I18N.as("spigotconfig.18");
    }

    @ModifyArg(method = "messages", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotConfig;getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 3), index = 1)
    private static String bosom$messages3(String path) {
        return TaiyitistMCStart.I18N.as("spigotconfig.19");
    }

    @ModifyArg(method = "messages", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotConfig;getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", ordinal = 3), index = 1)
    private static String bosom$messages4(String path) {
        return TaiyitistMCStart.I18N.as("spigotconfig.20");
    }

    @ModifyArg(method = "readConfig", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 1), index = 1)
    private static String bosom$readConfig0(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.21");
    }

    @ModifyArg(method = "readConfig", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 0), index = 1)
    private static String bosom$readConfig1(String msg) {
        return TaiyitistMCStart.I18N.as("spigotconfig.22");
    }
}
