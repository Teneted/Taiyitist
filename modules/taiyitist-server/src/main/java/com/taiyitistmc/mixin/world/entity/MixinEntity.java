package com.taiyitistmc.mixin.world.entity;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.Codec;
import com.taiyitistmc.injection.world.entity.InjectionEntity;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.ActivationRange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    @Shadow @Nullable
    public abstract String getEncodeId();

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

    @Shadow public abstract void gameEvent(Holder<GameEvent> holder);

    @Shadow public abstract void gameEvent(Holder<GameEvent> holder, @Nullable Entity entity);

    @Shadow public abstract Level level();

    @Shadow protected abstract void removePassenger(Entity entity);

    @Shadow private ImmutableList<Entity> passengers;

    @Shadow public abstract boolean isSwimming();

    @Shadow @Final protected SynchedEntityData entityData;
    @Shadow @Final private static EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID;
    @Shadow private AABB bb;

    @Shadow public abstract boolean fireImmune();

    @Shadow public abstract boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f);

    @Shadow public abstract void teleportRelative(double d, double e, double f);

    @Shadow public abstract void stopRiding();

    @Shadow private EntityInLevelCallback levelCallback;

    @Shadow public abstract void onRemoval(Entity.RemovalReason removalReason);

    @Shadow @Nullable public abstract Entity teleport(TeleportTransition teleportTransition);

    @Shadow public abstract Component getName();

    @Shadow public abstract Component getDisplayName();

    @Shadow public abstract Vec2 getRotationVector();

    @Shadow public abstract boolean touchingUnloadedChunk();

    @Shadow public abstract AABB getBoundingBox();

    @Shadow public abstract boolean isPushedByFluid();

    @Shadow public abstract void setDeltaMovement(Vec3 vec3);

    @Shadow protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;
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

    // CraftBukkit start
    private CommandSource commandSource = new CommandSource() {

        @Override
        public void sendSystemMessage(Component ichatbasecomponent) {
        }

        @Override
        public CommandSender taiyitist$getBukkitSender(CommandSourceStack wrapper) {
            return getBukkitEntity();
        }

        @Override
        public boolean acceptsSuccess() {
            return ((ServerLevel) level()).getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return true;
        }
    };
    // CraftBukkit end


    @Override
    public CommandSource bridge$commandSource() {
        return commandSource;
    }

    @Override
    public void taiyitist$setCommandSource(CommandSource commandSource) {
        this.commandSource = commandSource;
    }

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

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void taiyitist$callEntityRemoveEvent(Entity.RemovalReason removalReason, CallbackInfo ci) {
        CraftEventFactory.callEntityRemoveEvent(((Entity) (Object) this), taiyitist$removeCause != null ? taiyitist$removeCause : null);
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

    @Inject(method = "shearOffAllLeashConnections", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;dropAllLeashConnections(Lnet/minecraft/world/entity/player/Player;)Z"))
    private void taiyitist$pushShearOffReason(Player player, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$unleashReason.set(EntityUnleashEvent.UnleashReason.SHEAR);
    }

    @Inject(method = "dropAllLeashConnections", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;dropLeash()V", ordinal = 0))
    private void taiyitist$dropLeashEvent(Player player, CallbackInfoReturnable<Boolean> cir) {
        this.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), taiyitist$unleashReason.get() != null ? taiyitist$unleashReason.get() : EntityUnleashEvent.UnleashReason.UNKNOWN)); // CraftBukkit
    }

    @Inject(method = "dropAllLeashConnections", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;dropLeash()V", ordinal = 1))
    private void taiyitist$dropLeashEvent0(Player player, CallbackInfoReturnable<Boolean> cir, @Local Leashable leashable2) {
        // CraftBukkit start
        if (leashable2 instanceof Entity entity) {
            this.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(entity.getBukkitEntity(), taiyitist$unleashReason.get() != null ? taiyitist$unleashReason.get() : EntityUnleashEvent.UnleashReason.UNKNOWN));
        }
        // CraftBukkit end
    }

    @Inject(method = "attemptToShearEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void taiyitist$markDrop(Player player, InteractionHand interactionHand, ItemStack itemStack, Mob mob, CallbackInfoReturnable<Boolean> cir) {
        this.forceDrops = true; // CraftBukkit
    }

    @Inject(method = "attemptToShearEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/PlayerInteractTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;)V"))
    private void taiyitist$markDrop0(Player player, InteractionHand interactionHand, ItemStack itemStack, Mob mob, CallbackInfoReturnable<Boolean> cir) {
        this.forceDrops = false; // CraftBukkit
    }

    @ModifyExpressionValue(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
    private boolean taiyitist$addBukkitFlag(boolean original, @Local(argsOnly = true) boolean bl) {
        return original && !bl;
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"), cancellable = true)
    private void taiyitist$handleEntityMountEvent(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (entity.getBukkitEntity() instanceof Vehicle && this.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity) {
            VehicleEnterEvent event = new VehicleEnterEvent((Vehicle) entity.getBukkitEntity(), this.getBukkitEntity());
            // Suppress during worldgen
            if (this.valid) {
                Bukkit.getPluginManager().callEvent(event);
            }
            if (event.isCancelled()) {
                cir.setReturnValue(false);
            }
        }

        EntityMountEvent event = new EntityMountEvent(this.getBukkitEntity(), entity.getBukkitEntity());
        // Suppress during worldgen
        if (this.valid) {
            Bukkit.getPluginManager().callEvent(event);
        }
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @Redirect(method = "removeVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;removePassenger(Lnet/minecraft/world/entity/Entity;)V"))
    private void taiyitist$setEntity(Entity instance, Entity entity) {
        if (!entity.taiyitist$removePassenger(((Entity) (Object) this))) this.vehicle = entity; // CraftBukkit
    }

    @Inject(method = "removePassenger", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;size()I"), cancellable = true)
    private void taiyitist$handleVehicleExitEvent(Entity entity, CallbackInfo ci) {
        // CraftBukkit start
        CraftEntity craft = (CraftEntity) entity.getBukkitEntity().getVehicle();
        Entity orig = craft == null ? null : craft.getHandle();
        if (getBukkitEntity() instanceof Vehicle && entity.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity) {
            VehicleExitEvent event = new VehicleExitEvent(
                    (Vehicle) getBukkitEntity(),
                    (org.bukkit.entity.LivingEntity) entity.getBukkitEntity()
            );
            // Suppress during worldgen
            if (this.valid) {
                Bukkit.getPluginManager().callEvent(event);
            }
            CraftEntity craftn = (CraftEntity) entity.getBukkitEntity().getVehicle();
            Entity n = craftn == null ? null : craftn.getHandle();
            if (event.isCancelled() || n != orig) {
                ci.cancel();
            }
        }

        EntityDismountEvent event = new EntityDismountEvent(entity.getBukkitEntity(), this.getBukkitEntity());
        // Suppress during worldgen
        if (this.valid) {
            Bukkit.getPluginManager().callEvent(event);
        }
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @ModifyExpressionValue(method = "handlePortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isLevelEnabled(Lnet/minecraft/world/level/Level;)Z"))
    private boolean taiyitist$checkPlayer(boolean original) {
        return original || ((Entity) (Object) this) instanceof ServerPlayer;
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void taiyitist$callToggleSwimEvent(boolean flag, CallbackInfo ci) {
        // CraftBukkit start
        if (valid && this.isSwimming() != flag && ((Entity) (Object) this) instanceof LivingEntity) {
            if (CraftEventFactory.callToggleSwimEvent((LivingEntity) (Object) this, flag).isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @WrapWithCondition(method = "setInvisible", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSharedFlag(IZ)V"))
    private boolean taiyitist$checkInvisibility(Entity instance, int i, boolean bl) {
        return !this.persistentInvisibility;// Prevent Minecraft from removing our invisibility flag
    }

    @ModifyReturnValue(method = "getMaxAirSupply", at = @At("RETURN"))
    private int taiyitist$useMaxAirTikcks(int original) {
        return maxAirTicks; // CraftBukkit - SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    }

    @Redirect(method = "setAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"))
    private void taiyitist$handleEntityAirChangeEvent(SynchedEntityData instance, EntityDataAccessor<Integer> entityDataAccessor, Object object, @Local(argsOnly = true) int i) {
        // CraftBukkit start
        EntityAirChangeEvent event = new EntityAirChangeEvent(this.getBukkitEntity(), i);
        // Suppress during worldgen
        if (this.valid) {
            event.getEntity().getServer().getPluginManager().callEvent(event);
        }
        if (event.isCancelled() && this.getAirSupply() != i) {
            this.entityData.markDirty(DATA_AIR_SUPPLY_ID);
            return;
        }
        this.entityData.set(DATA_AIR_SUPPLY_ID, event.getAmount());
        // CraftBukkit end
    }

    @Redirect(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void taiyitist$entityCombustEvent(Entity instance, float f,
                                              @Local(argsOnly = true) LightningBolt lightningBolt,
                                              @Share("taiyitist$thisBukkitEntity") LocalRef<org.bukkit.entity.Entity> taiyitist$thisBukkitEntity,
                                              @Share("taiyitist$stormBukkitEntity") LocalRef<org.bukkit.entity.Entity> taiyitist$stormBukkitEntity,
                                              @Share("taiyitist$pluginManager") LocalRef<PluginManager> taiyitist$pluginManager) {
        // CraftBukkit start
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        final org.bukkit.entity.Entity stormBukkitEntity = lightningBolt.getBukkitEntity();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        taiyitist$thisBukkitEntity.set(thisBukkitEntity);
        taiyitist$stormBukkitEntity.set(stormBukkitEntity);
        taiyitist$pluginManager.set(pluginManager);
        // CraftBukkit end
        // CraftBukkit start - Call a combust event when lightning strikes
        EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8.0F);
        pluginManager.callEvent(entityCombustEvent);
        if (!entityCombustEvent.isCancelled()) {
            this.igniteForSeconds(entityCombustEvent.getDuration(), false);
        }
        // CraftBukkit end
    }

    @Redirect(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean taiyitist$hangingEvent(Entity instance, ServerLevel serverLevel,
                                           DamageSource damageSource, float v,
                                           @Local(argsOnly = true) LightningBolt lightningBolt,
                                           @Share("taiyitist$thisBukkitEntity") LocalRef<org.bukkit.entity.Entity> taiyitist$thisBukkitEntity,
                                           @Share("taiyitist$stormBukkitEntity") LocalRef<org.bukkit.entity.Entity> taiyitist$stormBukkitEntity,
                                           @Share("taiyitist$pluginManager") LocalRef<PluginManager> taiyitist$pluginManager,
                                           @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        if (taiyitist$thisBukkitEntity.get() instanceof Hanging) {
            HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging) taiyitist$thisBukkitEntity.get(), taiyitist$stormBukkitEntity.get());
            taiyitist$pluginManager.get().callEvent(hangingEvent);

            if (hangingEvent.isCancelled()) {
                ci.cancel();
            }
        }

        if (this.fireImmune()) {
            ci.cancel();
        }

        if (!this.hurtServer(serverLevel, this.damageSources().lightningBolt().customEntityDamager(lightningBolt), 5.0F)) {
            ci.cancel();
        }
        // CraftBukkit end
        return true;
    }

    @Inject(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/TeleportTransition;newLevel()Lnet/minecraft/server/level/ServerLevel;"), cancellable = true)
    private void taiyitist$handleTpEvent(TeleportTransition teleportTransition, CallbackInfoReturnable<Entity> cir) {
        // CraftBukkit start
        PositionMoveRotation absolutePosition = PositionMoveRotation.calculateAbsolute(PositionMoveRotation.of(((Entity) (Object) this)), PositionMoveRotation.of(teleportTransition), teleportTransition.relatives());
        Location to = CraftLocation.toBukkit(absolutePosition.position(), teleportTransition.newLevel().getWorld(), absolutePosition.yRot(), absolutePosition.xRot());
        EntityTeleportEvent teleEvent = CraftEventFactory.callEntityTeleportEvent(((Entity) (Object) this), to);
        if (teleEvent.isCancelled()) {
            cir.setReturnValue(null);
        }
        if (!to.equals(teleEvent.getTo())) {
            to = teleEvent.getTo();
            teleportTransition = new TeleportTransition(((CraftWorld) to.getWorld()).getHandle(), CraftLocation.toVec3D(to), Vec3.ZERO, to.getYaw(), to.getPitch(), teleportTransition.missingRespawnBlock(), teleportTransition.asPassenger(), Set.of(), teleportTransition.postTeleportTransition());
            teleportTransition.setTeleportCause(teleportTransition.getTeleportCause());
        }
        // CraftBukkit end
    }

    @Inject(method = "teleportCrossDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportSetPosition(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V"))
    private void taiyitist$forwardEntity(ServerLevel serverLevel, ServerLevel serverLevel2, TeleportTransition teleportTransition, CallbackInfoReturnable<Entity> cir, @Local(ordinal = 1) Entity entity) {
        // CraftBukkit start - Forward the CraftEntity to the new entity
        this.getBukkitEntity().setHandle(entity);
        entity.taiyitist$setBukkitEntity(this.getBukkitEntity());
        // CraftBukkit end
    }

    @WrapWithCondition(method = "teleportCrossDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addDuringTeleport(Lnet/minecraft/world/entity/Entity;)V"))
    private boolean taiyitist$checkInWorld(ServerLevel instance, Entity entity) {
        return this.inWorld;
    }

    @Inject(method = "removeAfterChangingDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$pushremoveAfterChangingDimensionsCause(CallbackInfo ci) {
        pushRemoveCause(null);
    }

    @Inject(method = "removeAfterChangingDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;removeLeash()V"))
    private void taiyitist$handleEntityUnleashEvent(CallbackInfo ci) {
        this.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), taiyitist$unleashReason.get() != null ? taiyitist$unleashReason.get() : EntityUnleashEvent.UnleashReason.UNKNOWN)); // CraftBukkit
    }

    @Redirect(method = "setBoundingBox", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;bb:Lnet/minecraft/world/phys/AABB;"))
    private void taiyitist$resetBB(Entity instance, AABB value) {
        // CraftBukkit start - block invalid bounding boxes
        double minX = value.minX,
                minY = value.minY,
                minZ = value.minZ,
                maxX = value.maxX,
                maxY = value.maxY,
                maxZ = value.maxZ;
        double len = value.maxX - value.minX;
        if (len < 0) maxX = minX;
        if (len > 64) maxX = minX + 64.0;

        len = value.maxY - value.minY;
        if (len < 0) maxY = minY;
        if (len > 64) maxY = minY + 64.0;

        len = value.maxZ - value.minZ;
        if (len < 0) maxZ = minZ;
        if (len > 64) maxZ = minZ + 64.0;
        this.bb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public CraftPortalEvent callPortalEvent(Entity entity, Location exit, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        Location enter = bukkitEntity.getLocation();

        EntityPortalEvent event = new EntityPortalEvent(bukkitEntity, enter, exit, searchRadius, true, creationRadius);
        event.getEntity().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !entity.isAlive()) {
            return null;
        }
        return new CraftPortalEvent(event);
    }
    // CraftBukkit end

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z", at = @At("HEAD"))
    private void taiyitist$tpCause(ServerLevel serverLevel, double d, double e, double f, Set<Relative> set, float g, float h, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        // Taiyitist - TODO fixme
    }

    @Redirect(method = "createCommandSourceStackForNameResolution", at = @At(value = "NEW", target = "(Lnet/minecraft/commands/CommandSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/server/level/ServerLevel;ILjava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/commands/CommandSourceStack;"))
    private CommandSourceStack taiyitist$resetSource(CommandSource commandSource, Vec3 vec3, Vec2 vec2, ServerLevel serverLevel, int i, String string, Component component, MinecraftServer minecraftServer, Entity entity) {
        return new CommandSourceStack(commandSource, this.position(), this.getRotationVector(), serverLevel, 0, this.getName().getString(), this.getDisplayName(), serverLevel.getServer(), ((Entity) (Object) this));
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tagKey, double d) {
        if (this.touchingUnloadedChunk()) {
            return false;
        } else {
            AABB aABB = this.getBoundingBox().deflate(0.001);
            int i = Mth.floor(aABB.minX);
            int j = Mth.ceil(aABB.maxX);
            int k = Mth.floor(aABB.minY);
            int l = Mth.ceil(aABB.maxY);
            int m = Mth.floor(aABB.minZ);
            int n = Mth.ceil(aABB.maxZ);
            double e = 0.0;
            boolean bl = this.isPushedByFluid();
            boolean bl2 = false;
            Vec3 vec3 = Vec3.ZERO;
            int o = 0;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for(int p = i; p < j; ++p) {
                for(int q = k; q < l; ++q) {
                    for(int r = m; r < n; ++r) {
                        mutableBlockPos.set(p, q, r);
                        FluidState fluidState = this.level().getFluidState(mutableBlockPos);
                        if (fluidState.is(tagKey)) {
                            double f = (double)((float)q + fluidState.getHeight(this.level(), mutableBlockPos));
                            if (f >= aABB.minY) {
                                bl2 = true;
                                e = Math.max(f - aABB.minY, e);
                                if (bl) {
                                    Vec3 vec32 = fluidState.getFlow(this.level(), mutableBlockPos);
                                    if (e < 0.4) {
                                        vec32 = vec32.scale(e);
                                    }

                                    vec3 = vec3.add(vec32);
                                    ++o;
                                }
                                // CraftBukkit start - store last lava contact location
                                if (tagKey == FluidTags.LAVA) {
                                    this.lastLavaContact = mutableBlockPos.immutable();
                                }
                                // CraftBukkit end
                            }
                        }
                    }
                }
            }

            if (vec3.length() > 0.0) {
                if (o > 0) {
                    vec3 = vec3.scale(1.0 / (double)o);
                }

                if (!(((Entity) (Object) this) instanceof Player)) {
                    vec3 = vec3.normalize();
                }

                Vec3 vec33 = this.getDeltaMovement();
                vec3 = vec3.scale(d);
                double g = 0.003;
                if (Math.abs(vec33.x) < 0.003 && Math.abs(vec33.z) < 0.003 && vec3.length() < 0.0045000000000000005) {
                    vec3 = vec3.normalize().scale(0.0045000000000000005);
                }

                this.setDeltaMovement(this.getDeltaMovement().add(vec3));
            }

            this.fluidHeight.put(tagKey, e);
            return bl2;
        }
    }

    @Override
    public boolean teleportTo(ServerLevel serverLevel, double d, double e, double f, Set<Relative> set, float g, float h, boolean bl, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause) {
        Entity entity = this.teleport(new TeleportTransition(serverLevel, new Vec3(d, e, f), Vec3.ZERO, g, h, set, TeleportTransition.DO_NOTHING));
        return entity != null;
    }

    @Override
    public boolean taiyitist$removePassenger(Entity entity) {
        if (entity.getVehicle() == ((Entity) (Object) this)) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        } else {
            if (this.passengers.size() == 1 && this.passengers.get(0) == entity) {
                this.passengers = ImmutableList.of();
            } else {
                // CraftBukkit start
                CraftEntity craft = (CraftEntity) entity.getBukkitEntity().getVehicle();
                Entity orig = craft == null ? null : craft.getHandle();
                if (getBukkitEntity() instanceof Vehicle && entity.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity) {
                    VehicleExitEvent event = new VehicleExitEvent(
                            (Vehicle) getBukkitEntity(),
                            (org.bukkit.entity.LivingEntity) entity.getBukkitEntity()
                    );
                    // Suppress during worldgen
                    if (this.valid) {
                        Bukkit.getPluginManager().callEvent(event);
                    }
                    CraftEntity craftn = (CraftEntity) entity.getBukkitEntity().getVehicle();
                    Entity n = craftn == null ? null : craftn.getHandle();
                    if (event.isCancelled() || n != orig) {
                        return false;
                    }
                }

                EntityDismountEvent event = new EntityDismountEvent(entity.getBukkitEntity(), this.getBukkitEntity());
                // Suppress during worldgen
                if (this.valid) {
                    Bukkit.getPluginManager().callEvent(event);
                }
                if (event.isCancelled()) {
                    return false;
                }
                // CraftBukkit end
                this.passengers = (ImmutableList)this.passengers.stream().filter((entity2) -> {
                    return entity2 != entity;
                }).collect(ImmutableList.toImmutableList());
            }

            entity.boardingCooldown = 60;
            this.gameEvent(GameEvent.ENTITY_DISMOUNT, entity);
        }
        return true; // CraftBukkit
    }

    @Override
    public boolean dropAllLeashConnections(@Nullable Player player, EntityUnleashEvent.UnleashReason reason) {
        taiyitist$unleashReason.set(reason);
        List<Leashable> list = Leashable.leashableLeashedTo(((Entity) (Object) this));
        boolean bl = !list.isEmpty();
        if (this instanceof Leashable leashable) {
            if (leashable.isLeashed()) {
                this.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), reason)); // CraftBukkit
                leashable.dropLeash();
                bl = true;
            }
        }

        for (Leashable leashable2 : list) {
            // CraftBukkit start
            if (leashable2 instanceof Entity entity) {
                this.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(entity.getBukkitEntity(), reason));
            }
            // CraftBukkit end
            leashable2.dropLeash();
        }

        if (bl) {
            this.gameEvent(GameEvent.SHEAR, player);
            return true;
        } else {
            return false;
        }
    }

    private AtomicReference<EntityUnleashEvent.UnleashReason> taiyitist$unleashReason = new AtomicReference<>();

    @Override
    public void pushUnleashReason(EntityUnleashEvent.UnleashReason reason) {
        taiyitist$unleashReason.set(reason);
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
        CraftEventFactory.callEntityRemoveEvent(((Entity) (Object) this), cause);
        // CraftBukkit end
        if (this.removalReason == null) {
            this.removalReason = entity_removalreason;
        }

        if (this.removalReason.shouldDestroy()) {
            this.stopRiding();
        }

        this.getPassengers().forEach(Entity::stopRiding);
        this.levelCallback.onRemove(removalReason);
        this.onRemoval(removalReason);
        // CraftBukkit end
    }

    @Override
    public void discard(EntityRemoveEvent.Cause cause) {
        this.remove(Entity.RemovalReason.DISCARDED, cause);
        // CraftBukkit end
    }

    @Override
    public void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        taiyitist$removeCause = cause;
        CraftEventFactory.callEntityRemoveEvent(((Entity) (Object) this), cause);
        if (this.removalReason == null) {
            this.removalReason = entity_removalreason;
        }

        if (this.removalReason.shouldDestroy()) {
            this.stopRiding();
        }

        this.getPassengers().forEach(Entity::stopRiding);
        this.levelCallback.onRemove(removalReason);
        this.onRemoval(removalReason);
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
