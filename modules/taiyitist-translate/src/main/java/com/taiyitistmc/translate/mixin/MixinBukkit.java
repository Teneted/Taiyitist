package com.taiyitistmc.translate.mixin;

import java.util.logging.Logger;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.TaiyitistMCStart;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Bukkit.class, remap = false)
public abstract class MixinBukkit {

    @Shadow public static @NotNull String getName() {return null;}
    @Shadow public static @NotNull String getVersion() {return null;}
    @Shadow public static @NotNull String getBukkitVersion() {return null;}

    @Redirect(method = "setServer", at = @At(value = "INVOKE",
            target = "Ljava/util/logging/Logger;info(Ljava/lang/String;)V"))
    private static void bosom$i18nInfo(Logger instance, String msg, @Local(argsOnly = true) Server server) {
        server.getLogger().info(
                TaiyitistMCStart.I18N.as("bukkit.version.servername") + " "
                        + getName() + " "
                        + TaiyitistMCStart.I18N.as("bukkit.version.version") + " "
                        + getVersion() + " "
                        + TaiyitistMCStart.I18N.as("bukkit.version.apiversion") + " "
                        + getBukkitVersion() + ")");
    }
}