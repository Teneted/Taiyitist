package org.celestial_artistry.taiyitist.mixin.server;

import org.celestial_artistry.taiyitist.bukkit.BukkitSnapshotCaptures;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldLoader.class)
public class MixinWorldLoader {

    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/WorldLoader$WorldDataSupplier;get(Lnet/minecraft/server/WorldLoader$DataLoadContext;)Lnet/minecraft/server/WorldLoader$DataLoadOutput;"))
    private static WorldLoader.DataLoadContext taiyitist$captureContext(WorldLoader.DataLoadContext context) {
        BukkitSnapshotCaptures.captureDataLoadContext(context);
        return context;
    }
}
