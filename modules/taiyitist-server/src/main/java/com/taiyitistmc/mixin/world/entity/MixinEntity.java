package com.taiyitistmc.mixin.world.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.taiyitistmc.injection.world.entity.InjectionEntity;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.ActivationRange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Entity.class)
public abstract class MixinEntity implements InjectionEntity {

    @Shadow private Level level;
    @Shadow @Final public static int TOTAL_AIR_SUPPLY;

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow private float yRot;

    @Shadow public abstract SynchedEntityData getEntityData();

    @Shadow public abstract int getId();

    @Shadow public abstract void remove(Entity.RemovalReason removalReason);

    @Shadow protected abstract void handlePortal();

    @Shadow protected abstract SoundEvent getSwimSound();

    @Shadow protected abstract SoundEvent getSwimSplashSound();

    @Shadow protected abstract SoundEvent getSwimHighSpeedSplashSound();

    @Shadow public abstract boolean isPushable();

    @Shadow public abstract Pose getPose();

    @Shadow public abstract String getScoreboardName();

    @Shadow public abstract boolean isInLava();

    @Shadow private int remainingFireTicks;

    @Shadow public abstract DamageSources damageSources();

    @Shadow public abstract void igniteForTicks(int i);

    @Shadow public boolean horizontalCollision;

    @Shadow public abstract double getY();

    @Shadow @Nullable private Entity.RemovalReason removalReason;

    @Shadow @Nullable protected abstract String getEncodeId();

    @Shadow @Nullable private Entity vehicle;

    @Shadow public abstract Vec3 position();

    @Shadow public abstract Vec3 getDeltaMovement();

    @Shadow public abstract float getYRot();

    @Shadow public abstract float getXRot();

    @Shadow public double fallDistance;

    @Shadow public abstract int getAirSupply();

    @Shadow public abstract boolean onGround();

    @Shadow private boolean invulnerable;
    @Shadow private int portalCooldown;

    @Shadow public abstract UUID getUUID();

    @Shadow @Nullable public abstract Component getCustomName();

    @Shadow public abstract boolean isCustomNameVisible();

    @Shadow public abstract boolean isSilent();

    @Shadow public abstract boolean isNoGravity();

    @Shadow private boolean hasGlowingTag;

    @Shadow public abstract int getTicksFrozen();

    @Shadow private boolean hasVisualFire;
    @Shadow @Final private Set<String> tags;
    @Shadow @Final private static Codec<List<String>> TAG_LIST_CODEC;
    @Shadow private CustomData customData;
    @Shadow public abstract boolean isVehicle();

    @Shadow public abstract List<Entity> getPassengers();

    @Shadow public abstract void fillCrashReportCategory(CrashReportCategory crashReportCategory);

    @Shadow private float xRot;

    @Shadow public abstract int getMaxAirSupply();

    @Shadow public abstract void setInvisible(boolean bl);

    @Shadow protected abstract void addAdditionalSaveData(ValueOutput valueOutput);

    // CraftBukkit start
    private static final int CURRENT_LEVEL = 2;

    private static boolean isLevelAtLeast(ValueInput tag, int level) {
        int updateLevel = tag.getIntOr("Bukkit.updateLevel", -1);
        return updateLevel != -1 && tag.getIntOr("Bukkit.updateLevel", -1) >= level;
    }

    private CraftEntity bukkitEntity;

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(level.getCraftServer(), ((Entity) (Object) this));
        }
        return bukkitEntity;
    }

    // CraftBukkit - SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    @Override
    public int getDefaultMaxAirSupply() {
        return TOTAL_AIR_SUPPLY;
    }
    // CraftBukkit end

    // CraftBukkit start
    public boolean forceDrops;
    public boolean persist = true;
    public boolean visibleByDefault = true;
    public boolean valid;
    public boolean inWorld = false;
    public boolean generation;
    public int maxAirTicks = getDefaultMaxAirSupply(); // CraftBukkit - SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    public org.bukkit.projectiles.ProjectileSource projectileSource; // For projectiles only
    public boolean lastDamageCancelled; // SPIGOT-5339, SPIGOT-6252, SPIGOT-6777: Keep track if the event was canceled
    public boolean persistentInvisibility = false;
    public BlockPos lastLavaContact;
    // Marks an entity, that it was removed by a plugin via Entity#remove
    // Main use case currently is for SPIGOT-7487, preventing dropping of leash when leash is removed
    public boolean pluginRemoved = false;
    public final ActivationRange.ActivationType activationType =
            ActivationRange.initializeEntityActivationType((Entity) (Object) this);
    public long activatedTick = Integer.MIN_VALUE;
    public boolean defaultActivationState;

    @Override
    public float getBukkitYaw() {
        return this.yRot;
    }

    @Override
    public boolean isChunkLoaded() {
        return level.hasChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4);
    }
    // CraftBukkit end

    @Inject(method = "kill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$killReason(ServerLevel serverLevel, CallbackInfo ci) {
        pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
    }

    @Inject(method = "discard", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$discardReason(CallbackInfo ci) {
        pushRemoveCause(null);
    }

    @Inject(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$removeCause(Entity.RemovalReason removalReason, CallbackInfo ci) {
        pushRemoveCause(null);
    }

    @Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
    private void taiyitist$poseChangeEvent(Pose pose, CallbackInfo ci) {
        // CraftBukkit start
        if (pose == this.getPose()) {
            ci.cancel();
        }
        this.level.getCraftServer().getPluginManager().callEvent(new EntityPoseChangeEvent(this.getBukkitEntity(), org.bukkit.entity.Pose.values()[pose.ordinal()]));
        // CraftBukkit end
    }

    @Inject(method = "setRot", at = @At("HEAD"))
    private void taiyitist$setRot(float f, float f1, CallbackInfo ci) {
        // CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
            if (((Entity) (Object) this) instanceof ServerPlayer) {
                this.level.getCraftServer().getLogger().warning(this.getScoreboardName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)");
            }
            f = 0;
        }

        // pitch was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(f1)) {
            f1 = 0;
        }

        if (f1 == Float.POSITIVE_INFINITY || f1 == Float.NEGATIVE_INFINITY) {
            if (((Entity) (Object) this) instanceof ServerPlayer) {
                this.level.getCraftServer().getLogger().warning(this.getScoreboardName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)");
            }
            f1 = 0;
        }
        // CraftBukkit end
    }

    @WrapWithCondition(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;handlePortal()V"))
    private boolean taiyitist$checkIsPlayer(Entity instance) {
        return ((Entity) (Object) this) instanceof ServerPlayer;
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;checkBelowWorld()V"))
    private void taiyitist$setLavaContent(CallbackInfo ci) {
        if(!this.isInLava()) {
            this.lastLavaContact = null;
        }
    }

    @Redirect(method = "lavaIgnite", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void taiyitist$combustEvent(Entity instance, float f) {
        // CraftBukkit start - Fallen in lava TODO: this event spams!
        if (((Entity) (Object) this) instanceof LivingEntity && remainingFireTicks <= 0) {
            // not on fire yet
            org.bukkit.block.Block damager = (lastLavaContact == null) ? null : org.bukkit.craftbukkit.block.CraftBlock.at(level, lastLavaContact);
            org.bukkit.entity.Entity damagee = this.getBukkitEntity();
            EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
            this.level.getCraftServer().getPluginManager().callEvent(combustEvent);

            if (!combustEvent.isCancelled()) {
                this.igniteForSeconds(combustEvent.getDuration(), false);
            }
        } else {
            // This will be called every single tick the entity is in lava, so don't throw an event
            this.igniteForSeconds(15.0F, false);
        }
        // CraftBukkit end
    }

    @Redirect(method = "lavaHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;lava()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource taiyitist$addLavaContent(DamageSources instance) {
        return this.damageSources().lava().directBlock(level, lastLavaContact);
    }

    @Inject(method = "igniteForSeconds", at = @At("HEAD"))
    private void taiyitist$combustEvent0(float f, CallbackInfo ci) {
        EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), f);
        this.level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        f = event.getDuration();
        // CraftBukkit end
    }

    @Inject(method = "onBelowWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;discard()V"))
    private void taiyitist$onBelowWorldCause(CallbackInfo ci) {
        pushRemoveCause(EntityRemoveEvent.Cause.OUT_OF_WORLD); // CraftBukkit - add Bukkit remove cause
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private void taiyitist$handleVehicleBlockCollisionEvent(MoverType moverType, Vec3 vec3, CallbackInfo ci, @Local(ordinal = 1) Vec3 vec32) {
        // CraftBukkit start
        if (horizontalCollision && getBukkitEntity() instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();
            org.bukkit.block.Block bl = this.level.getWorld().getBlockAt(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));

            if (vec3.x > vec32.x) {
                bl = bl.getRelative(BlockFace.EAST);
            } else if (vec3.x < vec32.x) {
                bl = bl.getRelative(BlockFace.WEST);
            } else if (vec3.z > vec32.z) {
                bl = bl.getRelative(BlockFace.SOUTH);
            } else if (vec3.z < vec32.z) {
                bl = bl.getRelative(BlockFace.NORTH);
            }

            if (!bl.getType().isAir()) {
                VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                level.getCraftServer().getPluginManager().callEvent(event);
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "absSnapTo(DDD)V", at = @At("TAIL"))
    private void taiyitist$checkValid(double d, double e, double f, CallbackInfo ci) {
        if (valid) level.getChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4); // CraftBukkit
    }

    private AtomicBoolean taiyitist$includeAll = new AtomicBoolean(true);

    @Inject(method = "saveAsPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;saveWithoutId(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
    private void taiyitist$markIncludeAll(ValueOutput valueOutput, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$includeAll.getAndSet(true);
    }

    @Override
    public boolean saveAsPassenger(ValueOutput valueoutput, boolean includeAll) {
        taiyitist$includeAll.set(includeAll);
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        } else {
            String string = this.getEncodeId();
            if (!this.persist || string == null) { // CraftBukkit - persist flag
                return false;
            } else {
                valueoutput.putString("id", string);
                this.saveWithoutId(valueoutput, includeAll); // CraftBukkit - pass on includeAll
                return true;
            }
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;saveAsPassenger(Lnet/minecraft/world/level/storage/ValueOutput;)Z"))
    private void taiyitist$markIncludeAll(ValueOutput valueOutput, CallbackInfo ci) {
        taiyitist$includeAll.getAndSet(true);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V", ordinal = 3))
    private void taiyitist$checkRotation(ValueOutput valueOutput, CallbackInfo ci) {
        // CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
        // TODO: make sure this is the best way to address this.
        if (Float.isNaN(this.yRot)) {
            this.yRot = 0;
        }

        if (Float.isNaN(this.xRot)) {
            this.xRot = 0;
        }
        // CraftBukkit end
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V", ordinal = 4))
    private void taiyitist$setUUID(ValueOutput valueOutput, CallbackInfo ci) {
        // PAIL: Check above UUID reads 1.8 properly, ie: UUIDMost / UUIDLeast
        valueOutput.putLong("WorldUUIDLeast", ((ServerLevel) this.level).getWorld().getUID().getLeastSignificantBits());
        valueOutput.putLong("WorldUUIDMost", ((ServerLevel) this.level).getWorld().getUID().getMostSignificantBits());
        valueOutput.putInt("Bukkit.updateLevel", CURRENT_LEVEL);
        if (!this.persist) {
            valueOutput.putBoolean("Bukkit.persist", this.persist);
        }
        if (!this.visibleByDefault) {
            valueOutput.putBoolean("Bukkit.visibleByDefault", this.visibleByDefault);
        }
        if (this.persistentInvisibility) {
            valueOutput.putBoolean("Bukkit.invisible", this.persistentInvisibility);
        }
        // SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
        if (maxAirTicks != getDefaultMaxAirSupply()) {
            valueOutput.putInt("Bukkit.MaxAirSupply", getMaxAirSupply());
        }
        // CraftBukkit end
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void taiyitist$storeBukkitValues0(ValueOutput valueOutput, CallbackInfo ci) {
        // CraftBukkit start - stores eventually existing bukkit values
        if (this.bukkitEntity != null) {
            this.bukkitEntity.storeBukkitValues(valueOutput);
        }
        // CraftBukkit end
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void taiyitist$read$ReadBukkitValues(ValueInput valueinput, CallbackInfo ci) {

        // CraftBukkit start
        this.persist = valueinput.getBooleanOr("Bukkit.persist", this.persist);
        this.visibleByDefault = valueinput.getBooleanOr("Bukkit.visibleByDefault", this.visibleByDefault);
        // SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
        this.maxAirTicks = valueinput.getIntOr("Bukkit.MaxAirSupply", this.maxAirTicks);
        // CraftBukkit end

        // CraftBukkit start - Reset world
        if (((Entity) (Object) this) instanceof ServerPlayer) {
            Server server = Bukkit.getServer();
            org.bukkit.World bworld = null;

            // TODO: Remove World related checks, replaced with WorldUID
            String worldName = valueinput.getStringOr("world", "");

            Optional<Long> most = valueinput.getLong("WorldUUIDMost");
            Optional<Long> least = valueinput.getLong("WorldUUIDLeast");
            if (most.isPresent() && least.isPresent()) {
                UUID uid = new UUID(most.get(), least.get());
                bworld = server.getWorld(uid);
            } else {
                bworld = server.getWorld(worldName);
            }

            if (bworld == null) {
                bworld = ((org.bukkit.craftbukkit.CraftServer) server).getServer().getLevel(Level.OVERWORLD).getWorld();
            }

            ((ServerPlayer) (Object) this).setLevel(bworld == null ? null : ((CraftWorld) bworld).getHandle());
        }
        this.getBukkitEntity().readBukkitValues(valueinput);
        boolean bukkitInvisible = valueinput.getBooleanOr("Bukkit.invisible", false);
        if (bukkitInvisible) {
            this.setInvisible(bukkitInvisible);
            this.persistentInvisibility = bukkitInvisible;
        }
        // CraftBukkit end
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private void taiyitist$captureDeathDrops(ServerLevel serverLevel, ItemStack itemStack, Vec3 vec3, CallbackInfoReturnable<ItemEntity> cir) {
        // CraftBukkit start - Capture drops for death event
        if (((Entity) (Object) this) instanceof LivingEntity && !((LivingEntity) (Object) this).bridge$forceDrops()) {
            ((LivingEntity) (Object) this).bridge$drops().add(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemStack));
            cir.setReturnValue(null);
        }
        // CraftBukkit end
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void taiyitist$dropItemEvent(ServerLevel serverLevel, ItemStack itemStack, Vec3 vec3, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        // CraftBukkit start
        EntityDropItemEvent event = new EntityDropItemEvent(this.getBukkitEntity(), (org.bukkit.entity.Item) itemEntity.getBukkitEntity());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(null);
        }
        // CraftBukkit end
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z"), cancellable = true)
    private void taiyitist$callPlayerUnleashEntityEvent(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir, @Local Leashable leashable3) {
        // CraftBukkit start - fire PlayerUnleashEntityEvent
        if (CraftEventFactory.callPlayerUnleashEntityEvent(((Entity) (Object) this), player, interactionHand).isCancelled()) {
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket(((Entity) (Object) this), leashable3.getLeashHolder()));
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;isLeashed()Z"), cancellable = true)
    private void taiyitist$callPlayerUnleashEntityEvent0(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir, @Local Leashable leashable3) {
        // CraftBukkit start - fire PlayerLeashEntityEvent
        if (CraftEventFactory.callPlayerLeashEntityEvent(((Entity) (Object) this), player, player, interactionHand).isCancelled()) {
            ((ServerPlayer) player).resendItemInHarnds(); // SPIGOT-7615: Resend to fix client desync with used item
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket(((Entity) (Object) this), leashable3.getLeashHolder()));
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }


    // CraftBukkit start - allow excluding certain data when saving
    @Override
    public void addAdditionalSaveData(ValueOutput valueoutput, boolean includeAll) {
        addAdditionalSaveData(valueoutput);
    }
    // CraftBukkit end

    @Override
    public void saveWithoutId(ValueOutput valueOutput, boolean includeAll) {
        taiyitist$includeAll.set(includeAll);
        try {
            // CraftBukkit start - selectively save position
            if (includeAll) {
                if (this.vehicle != null) {
                    valueOutput.store("Pos", Vec3.CODEC, new Vec3(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
                } else {
                    valueOutput.store("Pos", Vec3.CODEC, this.position());
                }
            }
            // CraftBukkit end

            valueOutput.store("Motion", Vec3.CODEC, this.getDeltaMovement());
            // CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
            // TODO: make sure this is the best way to address this.
            if (Float.isNaN(this.yRot)) {
                this.yRot = 0;
            }

            if (Float.isNaN(this.xRot)) {
                this.xRot = 0;
            }
            // CraftBukkit end
            valueOutput.store("Rotation", Vec2.CODEC, new Vec2(this.getYRot(), this.getXRot()));
            valueOutput.putDouble("fall_distance", this.fallDistance);
            valueOutput.putShort("Fire", (short)this.remainingFireTicks);
            valueOutput.putShort("Air", (short)this.getAirSupply());
            valueOutput.putBoolean("OnGround", this.onGround());
            valueOutput.putBoolean("Invulnerable", this.invulnerable);
            valueOutput.putInt("PortalCooldown", this.portalCooldown);
            // CraftBukkit start - selectively save uuid and world
            if (includeAll) {
                valueOutput.store("UUID", UUIDUtil.CODEC, this.getUUID());
                // PAIL: Check above UUID reads 1.8 properly, ie: UUIDMost / UUIDLeast
                valueOutput.putLong("WorldUUIDLeast", ((ServerLevel) this.level).getWorld().getUID().getLeastSignificantBits());
                valueOutput.putLong("WorldUUIDMost", ((ServerLevel) this.level).getWorld().getUID().getMostSignificantBits());
            }
            valueOutput.putInt("Bukkit.updateLevel", CURRENT_LEVEL);
            if (!this.persist) {
                valueOutput.putBoolean("Bukkit.persist", this.persist);
            }
            if (!this.visibleByDefault) {
                valueOutput.putBoolean("Bukkit.visibleByDefault", this.visibleByDefault);
            }
            if (this.persistentInvisibility) {
                valueOutput.putBoolean("Bukkit.invisible", this.persistentInvisibility);
            }
            // SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
            if (maxAirTicks != getDefaultMaxAirSupply()) {
                valueOutput.putInt("Bukkit.MaxAirSupply", getMaxAirSupply());
            }
            // CraftBukkit end
            valueOutput.storeNullable("CustomName", ComponentSerialization.CODEC, this.getCustomName());
            if (this.isCustomNameVisible()) {
                valueOutput.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }

            if (this.isSilent()) {
                valueOutput.putBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                valueOutput.putBoolean("NoGravity", this.isNoGravity());
            }

            if (this.hasGlowingTag) {
                valueOutput.putBoolean("Glowing", true);
            }

            int i = this.getTicksFrozen();
            if (i > 0) {
                valueOutput.putInt("TicksFrozen", this.getTicksFrozen());
            }

            if (this.hasVisualFire) {
                valueOutput.putBoolean("HasVisualFire", this.hasVisualFire);
            }

            if (!this.tags.isEmpty()) {
                valueOutput.store("Tags", TAG_LIST_CODEC, List.copyOf(this.tags));
            }

            if (!this.customData.isEmpty()) {
                valueOutput.store("data", CustomData.CODEC, this.customData);
            }

            this.addAdditionalSaveData(valueOutput, includeAll); // CraftBukkit - pass on includeAll
            if (this.isVehicle()) {
                ValueOutput.ValueOutputList valueOutputList = valueOutput.childrenList("Passengers");
                Iterator var10 = this.getPassengers().iterator();

                while(var10.hasNext()) {
                    Entity entity = (Entity)var10.next();
                    ValueOutput valueOutput2 = valueOutputList.addChild();
                    if (!entity.saveAsPassenger(valueOutput2, includeAll)) { // CraftBukkit - pass on includeAll
                        valueOutputList.discardLast();
                    }
                }

                if (valueOutputList.isEmpty()) {
                    valueOutput.discard("Passengers");
                }
            }

            // CraftBukkit start - stores eventually existing bukkit values
            if (this.bukkitEntity != null) {
                this.bukkitEntity.storeBukkitValues(valueOutput);
            }
            // CraftBukkit end
        } catch (Throwable var7) {
            Throwable throwable = var7;
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being saved");
            this.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public final void igniteForSeconds(float f, boolean callEvent) {
        if (callEvent) {
            EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), f);
            this.level.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            f = event.getDuration();
        }
        // CraftBukkit end
        this.igniteForTicks(Mth.floor(f * 20.0F));
    }

    @Override
    public void remove(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        this.setRemoved(entity_removalreason, cause);
        // CraftBukkit end
    }

    @Override
    public void discard(EntityRemoveEvent.Cause cause) {
        this.remove(Entity.RemovalReason.DISCARDED, cause);
        // CraftBukkit end
    }

    // CraftBukkit end
    @Override
    public boolean bridge$inWorld() {
        return inWorld;
    }

    @Override
    public void taiyitist$setInWorld(boolean inWorld) {
        this.inWorld = inWorld;
    }

    @Override
    public void taiyitist$setBukkitEntity(CraftEntity bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    // CraftBukkit start
    @Override
    public void refreshEntityData(ServerPlayer to) {
        List<SynchedEntityData.DataValue<?>> list = this.getEntityData().getNonDefaultValues();

        if (list != null) {
            to.connection.send(new ClientboundSetEntityDataPacket(this.getId(), list));
        }
    }
    // CraftBukkit end

    @Override
    public boolean bridge$persist() {
        return persist;
    }

    @Override
    public void taiyitist$setPersist(boolean persist) {
        this.persist = persist;
    }

    @Override
    public boolean bridge$visibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public void taiyitist$setVisibleByDefault(boolean visibleByDefault) {
        this.visibleByDefault = visibleByDefault;
    }

    @Override
    public boolean bridge$valid() {
        return valid;
    }

    @Override
    public void taiyitist$setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int bridge$maxAirTicks() {
        return maxAirTicks;
    }

    @Override
    public void taiyitist$setMaxAirTicks(int maxAirTicks) {
        this.maxAirTicks = maxAirTicks;
    }

    @Override
    public ProjectileSource bridge$projectileSource() {
        return projectileSource;
    }

    @Override
    public void taiyitist$setProjectileSource(ProjectileSource projectileSource) {
        this.projectileSource = projectileSource;
    }

    @Override
    public boolean bridge$lastDamageCancelled() {
        return lastDamageCancelled;
    }

    @Override
    public void taiyitist$setLastDamageCancelled(boolean lastDamageCancelled) {
        this.lastDamageCancelled = lastDamageCancelled;
    }

    @Override
    public boolean bridge$persistentInvisibility() {
        return persistentInvisibility;
    }

    @Override
    public void taiyitist$setPersistentInvisibility(boolean persistentInvisibility) {
        this.persistentInvisibility = persistentInvisibility;
    }

    @Override
    public BlockPos bridge$lastLavaContact() {
        return lastLavaContact;
    }

    @Override
    public void taiyitist$setLastLavaContact(BlockPos lastLavaContact) {
        this.lastLavaContact = lastLavaContact;
    }

    @Override
    // CraftBukkit start
    public void postTick() {
        // No clean way to break out of ticking once the entity has been copied to a new world, so instead we move the portalling later in the tick cycle
        if (!(((Entity) (Object) this) instanceof ServerPlayer)) {
            this.handlePortal();
        }
    }
    // CraftBukkit end


    @Override
    public SoundEvent getSwimSound0() {
        return getSwimSound();
    }

    @Override
    public SoundEvent getSwimSplashSound0() {
        return getSwimSplashSound();
    }

    @Override
    public SoundEvent getSwimHighSpeedSplashSound0() {
        return getSwimHighSpeedSplashSound();
    }

    @Override
    // CraftBukkit start - collidable API
    public boolean canCollideWithBukkit(Entity entity) {
        return isPushable();
    }
    // CraftBukkit end


    @Override
    public ActivationRange.ActivationType bridge$activationType() {
        return activationType;
    }

    @Override
    public long bridge$activatedTick() {
        return activatedTick;
    }

    @Override
    public void taiyitist$setActivatedTick(long activatedTick) {
        this.activatedTick = activatedTick;
    }

    @Override
    public boolean bridge$defaultActivationState() {
        return defaultActivationState;
    }

    @Override
    public void taiyitist$setDefaultActivationState(boolean state) {
        this.defaultActivationState = state;
    }

    @Override
    public boolean bridge$generation() {
        return generation;
    }

    @Override
    public void taiyitist$setGeneration(boolean gen) {
        this.generation = gen;
    }

    private transient EntityRemoveEvent.Cause taiyitist$removeCause;
    private transient CreatureSpawnEvent.SpawnReason taiyitist$spawnReason;

    @Override
    public void pushRemoveCause(EntityRemoveEvent.Cause cause) {
        this.taiyitist$removeCause = cause;
    }

    @Override
    public void pushSpawnCause(CreatureSpawnEvent.SpawnReason reason) {
        this.taiyitist$spawnReason = reason;
    }

    @Override
    public boolean bridge$pluginRemoved() {
        return pluginRemoved;
    }

    @Override
    public void taiyitist$setPluginRemoved(boolean pluginRemoved) {
        this.pluginRemoved = pluginRemoved;
    }
}
