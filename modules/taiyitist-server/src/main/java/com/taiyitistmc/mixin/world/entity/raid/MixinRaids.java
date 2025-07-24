package com.taiyitistmc.mixin.world.entity.raid;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(Raids.class)
public abstract class MixinRaids extends SavedData {

    @Shadow protected abstract Raid getOrCreateRaid(ServerLevel serverLevel, BlockPos blockPos);

    @Shadow @Final private Int2ObjectMap<Raid> raidMap;

    @Shadow protected abstract int getUniqueId();

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    @Nullable
    public Raid createOrExtendRaid(ServerPlayer serverPlayer, BlockPos blockPos) {
        if (serverPlayer.isSpectator()) {
            return null;
        } else {
            ServerLevel serverLevel = serverPlayer.level();
            if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                return null;
            } else {
                DimensionType dimensionType = serverLevel.dimensionType();
                if (!dimensionType.hasRaids()) {
                    return null;
                } else {
                    List<PoiRecord> list = serverLevel.getPoiManager().getInRange((holder) -> {
                        return holder.is(PoiTypeTags.VILLAGE);
                    }, blockPos, 64, PoiManager.Occupancy.IS_OCCUPIED).toList();
                    int i = 0;
                    Vec3 vec3 = Vec3.ZERO;

                    for(Iterator var8 = list.iterator(); var8.hasNext(); ++i) {
                        PoiRecord poiRecord = (PoiRecord)var8.next();
                        BlockPos blockPos2 = poiRecord.getPos();
                        vec3 = vec3.add((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
                    }

                    BlockPos blockPos3;
                    if (i > 0) {
                        vec3 = vec3.scale(1.0 / (double)i);
                        blockPos3 = BlockPos.containing(vec3);
                    } else {
                        blockPos3 = blockPos;
                    }

                    Raid raid = this.getOrCreateRaid(serverLevel, blockPos3);
                    /* CraftBukkit - moved down
                    if (!raid.isStarted() && !this.raidMap.containsValue(raid)) {
                        this.raidMap.put(this.getUniqueId(), raid);
                    }
                    */

                    if (!raid.isStarted()  || (raid.isInProgress() && raid.getRaidOmenLevel() < raid.getMaxRaidOmenLevel())) { // CraftBukkit - fixed a bug with raid: players could add up Bad Omen level even when the raid had finished
                        // CraftBukkit start
                        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callRaidTriggerEvent(raid, serverLevel, serverPlayer)) {
                            serverPlayer.removeEffect(net.minecraft.world.effect.MobEffects.RAID_OMEN);
                            return null;
                        }

                        if (!raid.isStarted() && !this.raidMap.containsValue(raid)) {
                            this.raidMap.put(this.getUniqueId(), raid);
                        }
                        // CraftBukkit end
                        raid.absorbRaidOmen(serverPlayer);
                    }

                    this.setDirty();
                    return raid;
                }
            }
        }
    }
}
