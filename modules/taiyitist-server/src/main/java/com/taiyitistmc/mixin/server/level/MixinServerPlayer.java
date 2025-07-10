package com.taiyitistmc.mixin.server.level;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import com.taiyitistmc.bukkit.DoubleChestInventory;
import com.taiyitistmc.injection.server.level.InjectionServerPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.BlockUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.MainHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements InjectionServerPlayer {

    @Shadow
    public int lastSentExp;
    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow
    @Final
    public ServerPlayerGameMode gameMode;
    @Shadow
    public ServerGamePacketListenerImpl connection;
    // CraftBukkit start
    public String displayName;
    public Component listName;
    public Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    public boolean sentListPacket = false;
    public Integer clientViewDistance;
    public String kickLeaveMessage = null; // SPIGOT-3034: Forward leave message to PlayerQuitEvent
    public long timeOffset = 0;
    public WeatherType weather = null;
    public boolean relativeTime = true;
    public String locale = null; // CraftBukkit - add, lowercase // Paper - default to null
    public CraftPlayer.TransferCookieConnection transferCookieConnection;
    @Shadow
    @Nullable
    private Entity camera;
    @Shadow
    private int containerCounter;
    private boolean taiyitist$initialized = false;
    private float pluginRainPosition;
    private float pluginRainPositionPrevious;
    private transient PlayerSpawnChangeEvent.Cause taiyitist$spawnChangeCause;
    private final AtomicReference<HorseInventoryMenu> taiyitist$horseMenu = new AtomicReference<>();
    private final AtomicReference<String> taiyitist$deathString = new AtomicReference<>("null");
    private final AtomicReference<String> taiyitist$deathMsg = new AtomicReference<>("null");
    private final AtomicReference<PlayerDeathEvent> taiyitist$deathEvent = new AtomicReference<>();
    private final AtomicReference<PlayerTeleportEvent.TeleportCause> taiyitist$changeDimensionCause = new AtomicReference<>(PlayerTeleportEvent.TeleportCause.UNKNOWN);
    // CraftBukkit end
    private transient BlockStateListPopulator taiyitist$populator;

    public MixinServerPlayer(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow
    protected abstract boolean bedInRange(BlockPos pos, Direction direction);

    @Shadow
    protected abstract boolean bedBlocked(BlockPos pos, Direction direction);

    @Shadow
    protected abstract int getCoprime(int i);

    @Shadow
    public abstract void setServerLevel(ServerLevel serverLevel);

    @Shadow
    public abstract void initMenu(AbstractContainerMenu abstractContainerMenu);
    @Shadow
    public abstract void setCamera(@Nullable Entity entityToSpectate);

    @Shadow
    public abstract void resetFallDistance();

    @Shadow
    public abstract boolean canHarmPlayer(Player other);

    @Shadow @Nullable private ServerPlayer.RespawnConfig respawnConfig;

    @Shadow public abstract TeleportTransition findRespawnPositionAndUseSpawnBlock(boolean bl, TeleportTransition.PostTeleportTransition postTeleportTransition);

    @Shadow public abstract void snapTo(double d, double e, double f);

    @Shadow public abstract void setRespawnPosition(@Nullable ServerPlayer.RespawnConfig respawnConfig, boolean bl);

    @Shadow public abstract ServerLevel level();

    @Override
    public void taiyitist$setTransferCookieConnection(CraftPlayer.TransferCookieConnection transferCookieConnection) {
        this.transferCookieConnection = transferCookieConnection;
    }

    @Override
    public CraftPlayer.TransferCookieConnection bridge$transferCookieConnection() {
        return this.transferCookieConnection;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void taiyitist$init(CallbackInfo ci) {
        this.displayName = getScoreboardName();
        this.taiyitist$setBukkitPickUpLoot(true);
        this.maxHealthCache = this.getMaxHealth();
        this.taiyitist$initialized = true;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$readExtra(ValueInput valueInput, CallbackInfo ci) {
        this.getBukkitEntity().readExtraData(valueInput);
        String spawnWorld = valueInput.getStringOr("SpawnWorld", "");
        CraftWorld oldWorld = (CraftWorld) Bukkit.getWorld(spawnWorld);
        if (oldWorld != null) {
            ServerPlayer.RespawnConfig respawnConfig = this.respawnConfig;
            this.respawnConfig = new ServerPlayer.RespawnConfig(oldWorld.getHandle().dimension(), respawnConfig.pos(), respawnConfig.angle(), respawnConfig.forced());
        }
    }

    @Redirect(method = "saveParentVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hasExactlyOnePlayerPassenger()Z"))
    private boolean taiyitist$nonPersistVehicle(Entity entity) {
        Entity entity1 = this.getVehicle();
        boolean persistVehicle = true;
        if (entity1 != null) {
            Entity vehicle;
            for (vehicle = entity1; vehicle != null; vehicle = vehicle.getVehicle()) {
                if (!vehicle.bridge$persist()) {
                    persistVehicle = false;
                    break;
                }
            }
        }
        return persistVehicle && entity.hasExactlyOnePlayerPassenger();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$writeExtra(ValueOutput valueOutput, CallbackInfo ci) {
        this.getBukkitEntity().setExtraData(valueOutput);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void taiyitist$joining(CallbackInfo ci) {
        if (this.joining) {
            this.joining = false;
        }
    }

    @Redirect(method = "doTick", at = @At(value = "NEW", args = "class=net/minecraft/network/protocol/game/ClientboundSetHealthPacket"))
    private ClientboundSetHealthPacket taiyitist$useScaledHealth(float healthIn, int foodLevelIn, float saturationLevelIn) {
        return new ClientboundSetHealthPacket(this.getBukkitEntity().getScaledHealth(), foodLevelIn, saturationLevelIn);
    }

    @Inject(method = "doTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;tickCount:I"))
    private void taiyitist$updateHealthAndExp(CallbackInfo ci) {
        if (this.maxHealthCache != this.getMaxHealth()) {
            this.getBukkitEntity().updateScaledHealth();
        }
        if (this.bridge$oldLevel() == -1) {
            this.taiyitist$setOldLevel(this.experienceLevel);
        }
        if (this.bridge$oldLevel() != this.experienceLevel) {
            CraftEventFactory.callPlayerLevelChangeEvent(this.getBukkitEntity(), this.bridge$oldLevel(), this.experienceLevel);
            this.taiyitist$setOldLevel(this.experienceLevel);
        }
        if (this.getBukkitEntity().hasClientWorldBorder()) {
            ((CraftWorldBorder) this.getBukkitEntity().getWorldBorder()).getHandle().tick();
        }
    }

    @Redirect(method = "awardKillScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void taiyitist$useCustomScoreboard(Scoreboard instance, ObjectiveCriteria criteria, ScoreHolder scoreboardName, Consumer<ScoreAccess> points) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(criteria, scoreboardName, points);
    }

    @Redirect(method = "handleTeamKill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void taiyitist$teamKill(Scoreboard instance, ObjectiveCriteria criteria, ScoreHolder scoreboardName, Consumer<ScoreAccess> points) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(criteria, scoreboardName, points);
    }

    @Inject(method = "isPvpAllowed", cancellable = true, at = @At("HEAD"))
    private void taiyitist$pvpMode(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((this.level().bridge$pvpMode()));
    }

    // CraftBukkit start - World fallback code, either respawn location or global spawn
    @Override
    public void spawnIn(Level world, boolean flag) {
        this.setLevel(world);
        if (world == null) {
            this.unsetRemoved();
            TeleportTransition teleporttransition = this.findRespawnPositionAndUseSpawnBlock(!flag, TeleportTransition.DO_NOTHING/*, null*/);

            this.setLevel(teleporttransition.newLevel());
            this.setPos(teleporttransition.position());
        }
        this.gameMode.setLevel((ServerLevel) world);
    }

    // CraftBukkit end
    @Override
    public void resetPlayerWeather() {
        this.weather = null;
        this.setPlayerWeather(this.level().getLevelData().isRaining() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
    }

    @Override
    public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        if (this.weather == null) {
            if (oldRain != newRain) {
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, newRain));
            }
        } else if (this.pluginRainPositionPrevious != this.pluginRainPosition) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.pluginRainPosition));
        }
        if (oldThunder != newThunder) {
            if (this.weather == WeatherType.DOWNFALL || this.weather == null) {
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, newThunder));
            } else {
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 0.0f));
            }
        }
    }

    @Override
    public void tickWeather() {
        if (this.weather == null) {
            return;
        }
        this.pluginRainPositionPrevious = this.pluginRainPosition;
        if (this.weather == WeatherType.DOWNFALL) {
            this.pluginRainPosition += (float) 0.01;
        } else {
            this.pluginRainPosition -= (float) 0.01;
        }
        this.pluginRainPosition = Mth.clamp(this.pluginRainPosition, 0.0f, 1.0f);
    }

    @Override
    public void forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
        this.snapTo(x, y, z, yaw, pitch);
        this.connection.resetPosition();
    }

    @Override
    public void pushChangeSpawnCause(PlayerSpawnChangeEvent.Cause cause) {
        this.taiyitist$spawnChangeCause = cause;
    }

    @Override
    public void setPlayerWeather(WeatherType type, boolean plugin) {
        if (!plugin && this.weather != null) {
            return;
        }
        if (plugin) {
            this.weather = type;
        }
        if (type == WeatherType.DOWNFALL) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0f));
        } else {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
        }
    }

    @Override
    public BlockPos getSpawnPoint(ServerLevel worldserver) {
        BlockPos blockposition = worldserver.getSharedSpawnPos();

        if (worldserver.dimensionType().hasSkyLight() && worldserver.serverLevelData.getGameType() != GameType.ADVENTURE) {
            int i = Math.max(0, this.server.getSpawnRadius(worldserver));
            int j = Mth.floor(worldserver.getWorldBorder().getDistanceToBorder(blockposition.getX(), blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            long k = i * 2L + 1;
            long l = k * k;
            int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int) l;
            int j1 = this.getCoprime(i1);
            int k1 = RandomSource.create().nextInt(i1);

            for (int l1 = 0; l1 < i1; ++l1) {
                int i2 = (k1 + j1 * l1) % i1;
                int j2 = i2 % (i * 2 + 1);
                int k2 = i2 / (i * 2 + 1);
                BlockPos blockposition1 = PlayerRespawnLogic.getOverworldRespawnPos(worldserver, blockposition.getX() + j2 - i, blockposition.getZ() + k2 - i);

                if (blockposition1 != null) {
                    return blockposition1;
                }
            }
        }

        return blockposition;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.getBukkitEntity().getScoreboard().getHandle();
    }

    @Override
    public void reset() {
        float exp = 0.0f;
        if (this.keepLevel) {
            exp = this.experienceProgress;
            this.newTotalExp = this.totalExperience;
            this.newLevel = this.experienceLevel;
        }
        this.setHealth(this.getMaxHealth());
        this.stopUsingItem();
        this.setRemainingFireTicks(0);
        this.resetFallDistance();
        this.foodData = new FoodData();
        this.foodData.setEntityhuman((ServerPlayer) (Object) this);
        this.experienceLevel = this.newLevel;
        this.totalExperience = this.newTotalExp;
        this.experienceProgress = 0.0f;
        this.deathTime = 0;
        this.setArrowCount(0, true);
        this.removeAllEffects(EntityPotionEffectEvent.Cause.DEATH);
        this.effectsDirty = true;
        this.containerMenu = this.inventoryMenu;
        this.lastHurtByPlayer = null;
        this.lastHurtByMob = null;
        this.combatTracker = new CombatTracker((ServerPlayer) (Object) this);
        this.lastSentExp = -1;
        if (this.keepLevel) {
            this.experienceProgress = exp;
        } else {
            this.giveExperiencePoints(this.newExp);
        }
        this.keepLevel = false;
        this.setDeltaMovement(0, 0, 0);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void taiyitist$exhauseCause1(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.SWIM);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void taiyitist$exhauseCause2(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.WALK_UNDERWATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void taiyitist$exhauseCause3(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.WALK_ON_WATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void taiyitist$exhauseCause4(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.SPRINT);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void taiyitist$exhauseCause5(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.CROUCH);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 5, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void taiyitist$exhauseCause6(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.WALK);
    }

    @Inject(method = "doCloseContainer", at = @At("HEAD"))
    private void taiyitist$invClose(CallbackInfo ci) {
        if (this.containerMenu != this.inventoryMenu) {
            var old = BukkitSnapshotCaptures.getContainerOwner();
            BukkitSnapshotCaptures.captureContainerOwner((ServerPlayer) (Object) this);
            CraftEventFactory.handleInventoryCloseEvent((ServerPlayer) (Object) this);
            BukkitSnapshotCaptures.captureContainerOwner(old);
        }
    }


    @Redirect(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerRecipeBook;copyOverData(Lnet/minecraft/stats/ServerRecipeBook;)V"))
    private void taiyitist$copyOverData(ServerRecipeBook instance, ServerRecipeBook serverRecipeBook) {
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void taiyitist$handlePlayer(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        serverPlayer.getBukkitEntity().setHandle(((ServerPlayer) (Object) this));
        serverPlayer.taiyitist$setBukkitEntity(serverPlayer.getBukkitEntity());
    }

    @Redirect(method = "awardStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void taiyitist$addStats(Scoreboard instance, ObjectiveCriteria criteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> points) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(criteria, scoreHolder, points);
    }

    @Redirect(method = "resetStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void taiyitist$takeStats(Scoreboard instance, ObjectiveCriteria objectiveCriteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> consumer) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(objectiveCriteria, scoreHolder, consumer);
    }

    @Redirect(method = "updateScoreForCriteria", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void taiyitist$updateStats(Scoreboard instance, ObjectiveCriteria objectiveCriteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> consumer) {
        // CraftBukkit - Use our scores instead
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(objectiveCriteria, scoreHolder,
                consumer);
    }

    @Inject(method = "resetSentInfo", at = @At("HEAD"))
    private void taiyitist$setExpUpdate(CallbackInfo ci) {
        this.lastSentExp = -1;
    }

    @Inject(method = "setCamera",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z"))
    private void taiyitist$pushSpectiveTpReason(Entity entity, CallbackInfo ci) {
        this.connection.pushTeleportCause(PlayerTeleportEvent.TeleportCause.SPECTATE);
    }

    @Override
    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) super.getBukkitEntity();
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() || !getBukkitEntity().isOnline();
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.getScoreboardName() + " at " + this.getX() + "," + this.getY() + "," + this.getZ() + ")";
    }

    @Override
    public int nextContainerCounterInt() {
        this.containerCounter = this.containerCounter % 100 + 1;
        return containerCounter; // CraftBukkit
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public OptionalInt openMenu(@Nullable MenuProvider menu) {
        if (menu == null) {
            return OptionalInt.empty();
        } else {
            if (this.containerMenu != this.inventoryMenu) {
                this.closeContainer();
            }
            this.nextContainerCounterInt();
            AbstractContainerMenu abstractContainerMenu = menu.createMenu(this.containerCounter, this.getInventory(), this);
            if (abstractContainerMenu != null) {
                abstractContainerMenu.setTitle(menu.getDisplayName());
                boolean cancelled = false;
                BukkitSnapshotCaptures.captureContainerOwner((ServerPlayer) (Object) this);
                abstractContainerMenu = CraftEventFactory.callInventoryOpenEvent((ServerPlayer) (Object) this, abstractContainerMenu, cancelled);
                if (abstractContainerMenu == null && !cancelled) {
                    if (menu instanceof Container) {
                        ((Container) menu).stopOpen((ServerPlayer) (Object) this);
                    } else if (menu instanceof DoubleChestInventory) {
                        ((DoubleChestInventory) menu).inventorylargechest.stopOpen(this);
                    }
                    return OptionalInt.empty();
                }
            }
            if (abstractContainerMenu == null) {
                if (this.isSpectator()) {
                    this.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
                }

                return OptionalInt.empty();
            } else {
                this.connection.send(new ClientboundOpenScreenPacket(abstractContainerMenu.containerId, abstractContainerMenu.getType(), menu.getDisplayName()));
                this.initMenu(abstractContainerMenu);
                this.containerMenu = abstractContainerMenu;
                return OptionalInt.of(this.containerCounter);
            }
        }
    }

    @Inject(method = "openHorseInventory", at = @At("HEAD"), cancellable = true)
    private void taiyitist$menuEvent(AbstractHorse abstractHorse, Container container, CallbackInfo ci) {
        // CraftBukkit start - Inventory open hook
        this.nextContainerCounterInt();
        AbstractContainerMenu taiyitist$container = new HorseInventoryMenu(this.containerCounter, this.getInventory(), container, abstractHorse, abstractHorse.getInventoryColumns());
        taiyitist$horseMenu.set((HorseInventoryMenu) taiyitist$container);
        taiyitist$container.setTitle(abstractHorse.getDisplayName());
        taiyitist$container = CraftEventFactory.callInventoryOpenEvent(((ServerPlayer) (Object) this), taiyitist$container);
        if (taiyitist$container == null) {
            container.stopOpen(this);
            ci.cancel();
        }
    }

    @Redirect(method = "openHorseInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;nextContainerCounter()V"))
    private void taiyitist$cancelNext(ServerPlayer instance) {
    }

    @Redirect(method = "openHorseInventory", at = @At(value = "NEW", args = "class=net/minecraft/world/inventory/HorseInventoryMenu"))
    private HorseInventoryMenu taiyitist$resetHorseMenu(int i, Inventory inventory, Container container, AbstractHorse abstractHorse, int j) {
        return taiyitist$horseMenu.get();
    }

    @Inject(method = "closeContainer", at = @At("HEAD"))
    private void taiyitist$closeMenu(CallbackInfo ci) {
        if (this.containerMenu != this.inventoryMenu) {
            var old = BukkitSnapshotCaptures.getContainerOwner();
            BukkitSnapshotCaptures.captureContainerOwner(this);
            CraftEventFactory.handleInventoryCloseEvent(this);
            BukkitSnapshotCaptures.captureContainerOwner(old);
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z",
            ordinal = 0),
            cancellable = true)
    private void taiyitist$deathEvent(DamageSource damageSource, CallbackInfo ci) {
        // CraftBukkit start - fire PlayerDeathEvent
        if (this.isRemoved()) {
            ci.cancel();
        }
        List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<>(this.getInventory().getContainerSize());
        boolean keepInventory = this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || this.isSpectator();

        if (!keepInventory) {
            for (ItemStack item : this.getInventory().getContents()) {
                if (!item.isEmpty() && !EnchantmentHelper.has(item, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    loot.add(CraftItemStack.asCraftMirror(item));
                }
            }
        }
        // SPIGOT-5071: manually add player loot tables (SPIGOT-5195 - ignores keepInventory rule)
        this.dropFromLootTable(this.level(), damageSource, this.lastHurtByPlayerMemoryTime > 0);
        for (org.bukkit.inventory.ItemStack item : this.bridge$drops()) {
            loot.add(item);
        }
        this.bridge$drops().clear(); // SPIGOT-5188: make sure to clear

        Component defaultMessage = this.getCombatTracker().getDeathMessage();
        String deathmessage = defaultMessage.getString();
        taiyitist$deathMsg.set(deathmessage);
        keepLevel = keepInventory; // SPIGOT-2222: pre-set keepLevel
        PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(((ServerPlayer) (Object) this), damageSource, loot, deathmessage, keepInventory);
        taiyitist$deathEvent.set(event);

        // SPIGOT-943 - only call if they have an inventory open
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }

        String deathMessage = event.getDeathMessage();
        taiyitist$deathString.set(deathMessage);
    }

    @Inject(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"),
            cancellable = true)
    private void taiyitist$checkDead(DamageSource damageSource, CallbackInfo ci) {
        boolean taiyitist$flag = taiyitist$deathString.get() != null && !taiyitist$deathString.get().isEmpty();
        if (!taiyitist$flag) { // TODO: allow plugins to override?
            ci.cancel();
        }
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"))
    private Component taiyitist$restDeathMsg(CombatTracker instance) {
        Component taiyitist$component;
        if (taiyitist$deathString.get().equals(taiyitist$deathMsg.get())) {
            taiyitist$component = instance.getDeathMessage();
        } else {
            taiyitist$component = CraftChatMessage.fromStringOrNull(taiyitist$deathString.get());
        }
        return taiyitist$component;
    }

    @Redirect(method = "die",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void taiyitist$cancelDrop(ServerPlayer instance, ServerLevel serverLevel, DamageSource damageSource) {
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void taiyitist$useBukkitScore(Scoreboard instance, ObjectiveCriteria objectiveCriteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> consumer) {
        this.setCamera(((ServerPlayer) (Object) this)); // Remove spectated target
        // CraftBukkit end
        // CraftBukkit - Get our scores instead
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, scoreHolder, ScoreAccess::increment);
    }

    @Inject(method = "stopSleepInBed",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"))
    private void taiyitist$tpCauseExitBed(boolean wakeImmediately, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        this.connection.pushTeleportCause(PlayerTeleportEvent.TeleportCause.EXIT_BED);
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"), cancellable = true)
    private void taiyitist$exitBedEvent(boolean flag, boolean flag1, CallbackInfo ci) {
        if (!this.isSleeping()) ci.cancel(); // CraftBukkit - Can't leave bed if not in one!
        // CraftBukkit start - fire PlayerBedLeaveEvent
        CraftPlayer player = this.getBukkitEntity();
        BlockPos bedPosition = this.getSleepingPos().orElse(null);

        org.bukkit.block.Block bed;
        if (bedPosition != null) {
            bed = this.level().getWorld().getBlockAt(bedPosition.getX(), bedPosition.getY(), bedPosition.getZ());
        } else {
            bed = this.level().getWorld().getBlockAt(player.getLocation());
        }

        PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed, true);
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Override
    public void pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause cause) {
        taiyitist$changeDimensionCause.set(cause);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    @Nullable
    public Component getTabListDisplayName() {
        return listName; // CraftBukkit
    }

    @Override
    public long getPlayerTime() {
        if (this.relativeTime) {
            return this.level().getDayTime() + this.timeOffset;
        }
        return this.level().getDayTime() - this.level().getDayTime() % 24000L + this.timeOffset;
    }

    @Override
    public WeatherType getPlayerWeather() {
        return this.weather;
    }

    @Override
    public Component bridge$listName() {
        return listName;
    }

    @Override
    public void taiyitist$setListName(Component listName) {
        this.listName = listName;
    }

    @Override
    public Location bridge$compassTarget() {
        return compassTarget;
    }

    @Override
    public void taiyitist$setCompassTarget(Location compassTarget) {
        this.compassTarget = compassTarget;
    }

    @Override
    public int bridge$newExp() {
        return newExp;
    }

    @Override
    public void taiyitist$setNewExp(int newExp) {
        this.newExp = newExp;
    }

    @Override
    public int bridge$newLevel() {
        return newLevel;
    }

    @Override
    public void taiyitist$setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    @Override
    public int bridge$newTotalExp() {
        return newTotalExp;
    }

    @Override
    public void taiyitist$setNewTotalExp(int newTotalExp) {
        this.newTotalExp = newTotalExp;
    }

    @Override
    public boolean bridge$keepLevel() {
        return keepLevel;
    }

    @Override
    public void taiyitist$setKeepLevel(boolean keepLevel) {
        this.keepLevel = keepLevel;
    }

    @Override
    public double bridge$maxHealthCache() {
        return maxHealthCache;
    }

    @Override
    public void taiyitist$setMaxHealthCache(double maxHealthCache) {
        this.maxHealthCache = maxHealthCache;
    }

    @Override
    public boolean bridge$joining() {
        return joining;
    }

    @Override
    public void taiyitist$setJoining(boolean joining) {
        this.joining = joining;
    }

    @Override
    public boolean bridge$sentListPacket() {
        return sentListPacket;
    }

    @Override
    public void taiyitist$setSentListPacket(boolean sentListPacket) {
        this.sentListPacket = sentListPacket;
    }

    @Override
    public Integer bridge$clientViewDistance() {
        return clientViewDistance;
    }

    @Override
    public void taiyitist$setClientViewDistance(Integer clientViewDistance) {
        this.clientViewDistance = clientViewDistance;
    }

    @Override
    public String bridge$kickLeaveMessage() {
        return kickLeaveMessage;
    }

    @Override
    public void taiyitist$setKickLeaveMessage(String kickLeaveMessage) {
        this.kickLeaveMessage = kickLeaveMessage;
    }

    @Override
    public String bridge$displayName() {
        return displayName;
    }

    @Override
    public void taiyitist$setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public long bridge$timeOffset() {
        return timeOffset;
    }

    @Override
    public void taiyitist$setTimeOffset(long timeOffset) {
        this.timeOffset = timeOffset;
    }

    @Override
    public boolean bridge$relativeTime() {
        return relativeTime;
    }

    @Override
    public void taiyitist$setRelativeTime(boolean relativeTime) {
        this.relativeTime = relativeTime;
    }

    @Override
    public String bridge$locale() {
        return locale;
    }

    @Override
    public void taiyitist$setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean taiyitist$initialized() {
        return taiyitist$initialized;
    }
}
