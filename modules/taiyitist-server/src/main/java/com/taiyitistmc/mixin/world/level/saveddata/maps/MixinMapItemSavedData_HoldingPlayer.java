package com.taiyitistmc.mixin.world.level.saveddata.maps;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItemSavedData.HoldingPlayer.class)
public abstract class MixinMapItemSavedData_HoldingPlayer {

    @Shadow
    @Final
    public Player player;
    @Shadow
    @Final
    private MapItemSavedData field_132;
    @Unique
    private final byte[] taiyitist$colors = field_132.colors;
    @Unique
    private final Collection<MapDecoration> icons = new java.util.ArrayList<>();

    private final AtomicReference<RenderData> taiyitist$render = new AtomicReference<>();
    private final AtomicReference<Player> taiyitist$player = new AtomicReference<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$initRender(MapItemSavedData mapItemSavedData, Player player, CallbackInfo ci) {
        taiyitist$player.set(player);
    }

    @Inject(method = "nextUpdatePacket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$HoldingPlayer;createPatch()Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch;"))
    private void taiyitist$checkColors(MapId mapId, CallbackInfoReturnable<Packet<?>> cir) {
        RenderData render = field_132.bridge$mapView().render((CraftPlayer) this.taiyitist$player.getAndSet(null).getBukkitEntity()); // CraftBukkit
        taiyitist$render.set(render);
        field_132.colors = render.buffer;
    }

    @Inject(method = "nextUpdatePacket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$HoldingPlayer;createPatch()Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch;",
            shift = At.Shift.AFTER))
    private void taiyitist$setColors(MapId mapId, CallbackInfoReturnable<Packet<?>> cir) {
        field_132.colors = taiyitist$colors;
    }

    @Redirect(method = "nextUpdatePacket", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<MapDecoration> taiyitist$resetCollections(Map instance) {
        // CraftBukkit start
        for (org.bukkit.map.MapCursor cursor : taiyitist$render.getAndSet(null).cursors) {
            if (cursor.isVisible()) {
                icons.add(new MapDecoration(CraftMapCursor.CraftType.bukkitToMinecraftHolder(cursor.getType()), cursor.getX(), cursor.getY(), cursor.getDirection(), CraftChatMessage.fromStringOrOptional(cursor.getCaption())));
            }
        }
        return icons;
        // CraftBukkit end
    }
}