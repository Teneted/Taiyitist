package org.teneted.taiyitist.injection.world.entity;

import org.teneted.taiyitist.injection.world.level.entity.InjectionEntityAccess;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface InjectionEntity extends InjectionEntityAccess {

    default boolean bridge$inWorld() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setInWorld(boolean inWorld) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$invulnerableDuration() {
        throw new AssertionError("Not implemented");
    }

    default void taiyitist$setInvulnerableDuration(int invulnerableDuration) {
        throw new AssertionError("Not implemented");
    }

    default void taiyitist$setBukkitEntity(CraftEntity bukkitEntity) {
        throw new IllegalStateException("Not implemented");
    }

    default void refreshEntityData(ServerPlayer to) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$persist() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPersist(boolean persist) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$visibleByDefault() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setVisibleByDefault(boolean visibleByDefault) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$valid() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setValid(boolean valid) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$maxAirTicks() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxAirTicks(int maxAirTicks) {
        throw new IllegalStateException("Not implemented");
    }

    default org.bukkit.projectiles.ProjectileSource bridge$projectileSource() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setProjectileSource(org.bukkit.projectiles.ProjectileSource projectileSource) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$lastDamageCancelled() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setLastDamageCancelled(boolean lastDamageCancelled) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$persistentInvisibility() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPersistentInvisibility(boolean persistentInvisibility) {
        throw new IllegalStateException("Not implemented");
    }

    default BlockPos bridge$lastLavaContact() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setLastLavaContact(BlockPos lastLavaContact) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftEntity getBukkitEntity() {
        throw new IllegalStateException("Not implemented");
    }

    default int getDefaultMaxAirSupply() {
        throw new IllegalStateException("Not implemented");
    }

    default float getBukkitYaw() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isChunkLoaded() {
        throw new IllegalStateException("Not implemented");
    }

    default void postTick() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getSwimSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getSwimSplashSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getSwimHighSpeedSplashSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean canCollideWithBukkit(Entity entity) {
        throw new IllegalStateException("Not implemented");
    }

    default org.spigotmc.ActivationRange.ActivationType bridge$activationType() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean teleportTo(ServerLevel worldserver, double d0, double d1, double d2, Set<Relative> set, float f, float f1, boolean flag, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default long bridge$activatedTick() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setActivatedTick(long activatedTick) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$defaultActivationState() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setDefaultActivationState(boolean state) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$generation() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setGeneration(boolean gen) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean taiyitist$removePassenger(Entity entity) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftPortalEvent callPortalEvent(Entity entity, Location exit, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        throw new IllegalStateException("Not implemented");
    }

    default void discard(EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void remove(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    default void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushRemoveCause(EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushSpawnCause(CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default void igniteForSeconds(float i, boolean callEvent) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$pluginRemoved() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPluginRemoved(boolean pluginRemoved) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean saveAsPassenger(ValueOutput valueoutput, boolean includeAll) {
        throw new IllegalStateException("Not implemented");
    }

    default void saveWithoutId(ValueOutput valueoutput, boolean includeAll) {
        throw new IllegalStateException("Not implemented");
    }

    default void addAdditionalSaveData(ValueOutput valueOutput, boolean includeAll) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushUnleashReason(EntityUnleashEvent.UnleashReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean dropAllLeashConnections(@Nullable Player player, EntityUnleashEvent.UnleashReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default CommandSource bridge$commandSource() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCommandSource(CommandSource commandSource) {
        throw new IllegalStateException("Not implemented");
    }

    default void inactiveTick() {
        throw new IllegalStateException("Not implemented");
    }
}
