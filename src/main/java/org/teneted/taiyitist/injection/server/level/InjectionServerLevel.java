package org.teneted.taiyitist.injection.server.level;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.teneted.taiyitist.injection.world.level.InjectionLevel;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

public interface InjectionServerLevel extends InjectionLevel {

    default boolean addEntitySerialized(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default  <T extends ParticleOptions> int sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed, boolean force) {
        throw new IllegalStateException("Not implemented");
    }

    default LevelStorageSource.LevelStorageAccess bridge$convertable() {
        throw new IllegalStateException("Not implemented");
    }

    default UUID bridge$uuid() {
        throw new IllegalStateException("Not implemented");
    }

    default LevelChunk getChunkIfLoaded(int x, int z) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean addWithUUID(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default void addDuringTeleport(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean tryAddFreshEntityWithPassengers(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean strikeLightning(Entity entitylightning) {
        throw new AssertionError("Not implemented");
    }

    default boolean strikeLightning(Entity entitylightning, LightningStrikeEvent.Cause cause) {
        throw new AssertionError("Not implemented");
    }

    default  <T extends ParticleOptions> int sendParticles(ServerPlayer sender, T t0, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, boolean force) {
        throw new AssertionError("Not implemented");
    }

    default PrimaryLevelData bridge$serverLevelDataCB() {
        throw new AssertionError("Not implemented");
    }

    default boolean canAddFreshEntity() {
        throw new AssertionError("Not implemented");
    }

    default void sendParticlesSource(ServerPlayer serverPlayer, ParticleOptions particleParam, boolean force, boolean b, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        throw new AssertionError("Not implemented");
    }

    default WorldGenSettings getWorldGenSettings() {
        throw new AssertionError("Not implemented");
    }

    default Explosion explode0(Entity entity, DamageSource defaultDamageSource, Object o, double x, double y, double z, float power, boolean setFire, Level.ExplosionInteraction explosionType, SimpleParticleType explosion, SimpleParticleType explosionEmitter, WeightedList<ExplosionParticleInfo> defaultExplosionBlockParticles, Holder.Reference<SoundEvent> genericExplode) {
        throw new AssertionError("Not implemented");
    }
}
