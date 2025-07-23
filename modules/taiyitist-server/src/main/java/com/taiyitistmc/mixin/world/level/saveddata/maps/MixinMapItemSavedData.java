package com.taiyitistmc.mixin.world.level.saveddata.maps;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import com.taiyitistmc.injection.world.level.saveddata.maps.InjectionMapItemSavedData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

// TODO fixme patches
@Mixin(MapItemSavedData.class)
public abstract class MixinMapItemSavedData extends SavedData implements InjectionMapItemSavedData {

    @Shadow
    public ResourceKey<Level> dimension;
    // CraftBukkit start
    public CraftMapView mapView;
    public UUID uniqueId = null;
    public MapId id;

    @Inject(method = "<init>(IIBZZZLnet/minecraft/resources/ResourceKey;)V", at = @At("RETURN"))
    private void taiyitist$resetType(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, ResourceKey resourceKey, CallbackInfo ci) {
        // CraftBukkit start
        updateUUID();
        this.mapView = new CraftMapView(((MapItemSavedData) (Object) this));
    }

    private static ResourceKey<Level> getWorldKey(ResourceKey<Level> resourcekey, long uuidLeast, long uuidMost) {
        Level lookup = BukkitMethodHooks.getServer().getLevel(resourcekey);
        if (lookup != null) {
            return resourcekey;
        }

        if (uuidLeast != 0L && uuidMost != 0L) {
            UUID uniqueId = new UUID(uuidMost, uuidLeast);

            CraftWorld world = (CraftWorld) Bukkit.getWorld(uniqueId);
            // Check if the stored world details are correct.
            if (world == null) {
                /* All Maps which do not have their valid world loaded are set to a dimension which hopefully won't be reached.
                   This is to prevent them being corrupted with the wrong map data. */
                // PAIL: Use Vanilla exception handling for now
            } else {
                return world.getHandle().dimension();
            }
        }
        throw new IllegalArgumentException("Invalid map dimension: " + resourcekey);
    }

    @Nullable
    private UUID updateUUID() {
        if (this.uniqueId == null) {
            Level world = BukkitMethodHooks.getServer().getLevel(this.dimension);
            if (world != null) {
                this.uniqueId = world.getWorld().getUID();
            }
        }

        return this.uniqueId;
    }
    // CraftBukkit end
}
