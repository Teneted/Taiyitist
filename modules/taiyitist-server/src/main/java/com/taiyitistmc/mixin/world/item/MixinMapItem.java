package com.taiyitistmc.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.event.server.MapInitializeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItem.class)
public class MixinMapItem {

    // Banner TODO Map ID

    @Inject(method = "createNewSavedData", at = @At("RETURN"))
    private static void taiyitist$callMapEvent(ServerLevel serverLevel, int i, int j, int k, boolean bl, boolean bl2, ResourceKey<Level> resourceKey, CallbackInfoReturnable<MapId> cir, @Local MapItemSavedData mapItemSavedData) {
        // CraftBukkit start
        MapInitializeEvent event = new MapInitializeEvent(mapItemSavedData.bridge$mapView());
        Bukkit.getServer().getPluginManager().callEvent(event);
        // CraftBukkit end
    }
}
