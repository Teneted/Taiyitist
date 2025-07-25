package com.taiyitistmc.mixin.world.entity;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.ProcessableEffect;
import com.taiyitistmc.injection.world.entity.InjectionLivingEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Taiyitist - TODO finish mixin
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements InjectionLivingEntity {

    @Shadow public abstract float getYHeadRot();

    @Shadow @Final private AttributeMap attributes;

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Holder<Attribute> holder);

    @Shadow protected abstract Holder<SoundEvent> getEquipSound(EquipmentSlot equipmentSlot, net.minecraft.world.item.ItemStack itemStack, Equippable equippable);

    @Shadow protected abstract boolean doesEmitEquipEvent(EquipmentSlot equipmentSlot);

    @Shadow protected abstract void triggerOnDeathMobEffects(ServerLevel serverLevel, RemovalReason removalReason);

    @Shadow protected Brain<?> brain;

    @Shadow @Nullable protected abstract SoundEvent getHurtSound(DamageSource damageSource);

    @Shadow @Nullable protected abstract SoundEvent getDeathSound();

    @Shadow protected abstract SoundEvent getFallDamageSound(int i);

    @Shadow public abstract void swing(InteractionHand interactionHand);

    @Shadow @Nullable protected abstract ItemEntity createItemStackToDrop(net.minecraft.world.item.ItemStack itemStack, boolean bl, boolean bl2);

    @Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    @Shadow protected abstract void onEffectsRemoved(Collection<MobEffectInstance> collection);

    @Shadow public abstract boolean canBeAffected(MobEffectInstance mobEffectInstance);

    @Shadow protected abstract void onEffectAdded(MobEffectInstance mobEffectInstance, @Nullable Entity entity);

    @Shadow protected abstract void onEffectUpdated(MobEffectInstance mobEffectInstance, boolean bl, @Nullable Entity entity);
    @Shadow public abstract float getHealth();

    @Shadow public abstract void setHealth(float f);

    @Shadow public abstract boolean wasExperienceConsumed();

    @Shadow protected abstract boolean isAlwaysExperienceDropper();

    @Shadow protected int lastHurtByPlayerMemoryTime;

    @Shadow public abstract boolean shouldDropExperience();

    @Shadow public abstract int getExperienceReward(ServerLevel serverLevel, @Nullable Entity entity);

    @Shadow protected boolean dead;
    @Shadow public abstract boolean isSleeping();

    // CraftBukkit start
    public int expToDrop;
    public ArrayList<org.bukkit.inventory.ItemStack> drops = new ArrayList<org.bukkit.inventory.ItemStack>();
    public org.bukkit.craftbukkit.attribute.CraftAttributeMap craftAttributes;
    public boolean collides = true;
    public Set<UUID> collidableExemptions = new HashSet<>();
    public boolean bukkitPickUpLoot;
    public boolean forceDrops;
    // CraftBukkit start
    private boolean isTickingEffects = false;
    private List<ProcessableEffect> effectsToProcess = Lists.newArrayList();

    public MixinLivingEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getBukkitYaw() {
        return getYHeadRot();
    }
    // CraftBukkit end

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void taiyitist$muteHealth(LivingEntity entity, float health) {
        // do nothing
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        this.collides = true;
        this.craftAttributes = new CraftAttributeMap(this.attributes);
        // CraftBukkit - setHealth(getMaxHealth()) inlined and simplified to skip the instanceof check for EntityPlayer, as getBukkitEntity() is not initialized in constructor
        this.entityData.set(LivingEntity.DATA_HEALTH_ID, (float) this.getAttribute(Attributes.MAX_HEALTH).getValue());
    }

    @Redirect(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private <T extends ParticleOptions> int taiyitist$addCheckFall(ServerLevel instance, T particleOptions, double d, double e, double f, int i, double g, double h, double j, double k, @Local(argsOnly = true) BlockState blockState, @Local int l) {
        // CraftBukkit start - visiblity api
        if (((LivingEntity) (Object) this) instanceof ServerPlayer) {
            return instance.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), f, g, h, l, 0.0, 0.0, 0.0, 0.15000000596046448);
        } else {
            return instance.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), f, g, h, l, 0.0, 0.0, 0.0, 0.15000000596046448);
        }
        // CraftBukkit end
    }

    @Inject(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$tickDeathRemoveCause(CallbackInfo ci) {
        pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
    }

    private final AtomicBoolean taiyitist$silent = new AtomicBoolean(this.isSilent());

    @ModifyExpressionValue(method = "onEquipItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSilent()Z"))
    private boolean taiyitist$addSilentCheck(boolean original) {
        return original && !taiyitist$silent.get();
    }

    @Override
    public void onEquipItem(EquipmentSlot equipmentSlot, net.minecraft.world.item.ItemStack itemStack, net.minecraft.world.item.ItemStack itemStack2, boolean silent) {
        taiyitist$silent.set(silent);
        if (!this.level().isClientSide() && !this.isSpectator()) {
            if (!net.minecraft.world.item.ItemStack.isSameItemSameComponents(itemStack, itemStack2) && !this.firstTick) {
                Equippable equippable = (Equippable)itemStack2.get(DataComponents.EQUIPPABLE);
                if (!this.isSilent() && equippable != null && equipmentSlot == equippable.slot() && !silent) { // CraftBukkit
                    this.level().playSeededSound((Entity)null, this.getX(), this.getY(), this.getZ(), this.getEquipSound(equipmentSlot, itemStack2, equippable), this.getSoundSource(), 1.0F, 1.0F, this.random.nextLong());
                }

                if (this.doesEmitEquipEvent(equipmentSlot)) {
                    this.gameEvent(equippable != null ? GameEvent.EQUIP : GameEvent.UNEQUIP);
                }

            }
        }
    }

    private AtomicReference<EntityRemoveEvent.Cause> taiyitist$removeCause = new AtomicReference<>(EntityRemoveEvent.Cause.PLUGIN);

    @Inject(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$pushRemoveCause(RemovalReason removalReason, CallbackInfo ci) {
        pushRemoveCause(taiyitist$removeCause.get());
    }

    @Inject(method = "triggerOnDeathMobEffects", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
    private void taiyitist$pushDeathRemoveCause(ServerLevel serverLevel, RemovalReason removalReason, CallbackInfo ci) {
        pushEffectCause(EntityPotionEffectEvent.Cause.DEATH);
    }

    @Override
    public void remove(Entity.RemovalReason removalReason, EntityRemoveEvent.Cause cause) {
        taiyitist$removeCause.set(cause);
        if (removalReason == RemovalReason.KILLED || removalReason == RemovalReason.DISCARDED) {
            Level var3 = this.level();
            if (var3 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)var3;
                this.triggerOnDeathMobEffects(serverLevel, removalReason);
            }
        }

        super.remove(removalReason, cause); // CraftBukkit
        this.brain.clearMemories();
    }

    private AtomicReference<EntityPotionEffectEvent.Cause> taiyitist$effectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN);

    @Override
    public void pushEffectCause(EntityPotionEffectEvent.Cause cause) {
        taiyitist$effectCause.getAndSet(cause);
    }

    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void taiyitist$playerDropEvent(net.minecraft.world.item.ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        // CraftBukkit start - fire PlayerDropItemEvent
        if (((LivingEntity) (Object) this) instanceof ServerPlayer) {
            Player player = (Player) this.getBukkitEntity();
            org.bukkit.entity.Item drop = (org.bukkit.entity.Item) itemEntity.getBukkitEntity();

            PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                org.bukkit.inventory.ItemStack cur = player.getInventory().getItemInHand();
                if (bl2 && (cur == null || cur.getAmount() == 0)) {
                    // The complete stack was dropped
                    player.getInventory().setItemInHand(drop.getItemStack());
                } else if (bl2 && cur.isSimilar(drop.getItemStack()) && cur.getAmount() < cur.getMaxStackSize() && drop.getItemStack().getAmount() == 1) {
                    // Only one item is dropped
                    cur.setAmount(cur.getAmount() + 1);
                    player.getInventory().setItemInHand(cur);
                } else {
                    // Fallback
                    player.getInventory().addItem(drop.getItemStack());
                }
                cir.setReturnValue(null);
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void taiyitist$storeBukkitValue(ValueInput valueInput, CallbackInfo ci) {
        // CraftBukkit start
        float maxHealth = valueInput.getFloatOr("Bukkit.MaxHealth", -1);
        if (maxHealth > 0) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        }
        // CraftBukkit end
    }

    @Nullable
    @Override
    public ItemEntity drop(net.minecraft.world.item.ItemStack itemStack, boolean bl, boolean bl2, boolean callEvent) {
        if (itemStack.isEmpty()) {
            return null;
        } else if (this.level().isClientSide) {
            this.swing(InteractionHand.MAIN_HAND);
            return null;
        } else {
            ItemEntity itemEntity = this.createItemStackToDrop(itemStack, bl, bl2);
            if (itemEntity != null) {
                // CraftBukkit start - fire PlayerDropItemEvent
                if (callEvent && ((LivingEntity) (Object) this) instanceof ServerPlayer) {
                    Player player = (Player) this.getBukkitEntity();
                    org.bukkit.entity.Item drop = (org.bukkit.entity.Item) itemEntity.getBukkitEntity();

                    PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
                    this.level().getCraftServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        org.bukkit.inventory.ItemStack cur = player.getInventory().getItemInHand();
                        if (bl2 && (cur == null || cur.getAmount() == 0)) {
                            // The complete stack was dropped
                            player.getInventory().setItemInHand(drop.getItemStack());
                        } else if (bl2 && cur.isSimilar(drop.getItemStack()) && cur.getAmount() < cur.getMaxStackSize() && drop.getItemStack().getAmount() == 1) {
                            // Only one item is dropped
                            cur.setAmount(cur.getAmount() + 1);
                            player.getInventory().setItemInHand(cur);
                        } else {
                            // Fallback
                            player.getInventory().addItem(drop.getItemStack());
                        }
                        return null;
                    }
                }
                // CraftBukkit end
                this.level().addFreshEntity(itemEntity);
            }

            return itemEntity;
        }
    }

    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", shift = At.Shift.AFTER))
    private void taiyitist$markIsTicking(CallbackInfo ci) {
        isTickingEffects = true; // CraftBukkit
    }

    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"), cancellable = true)
    private void taiyitist$callEntityPotionEffectChangeEvent(CallbackInfo ci, @Local MobEffectInstance mobEffectInstance) {
        // CraftBukkit start
        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(((LivingEntity) (Object) this), mobEffectInstance, null, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.EXPIRATION);
        if (!event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "tickEffects", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;effectsDirty:Z", ordinal = 0))
    private void taiyitist$effectsToProcess(CallbackInfo ci) {
        // CraftBukkit start
        isTickingEffects = false;
        for (ProcessableEffect e : effectsToProcess) {
            if (e.getEffect() != null) {
                addEffect(e.getEffect(), e.getCause());
            } else {
                removeEffect(e.getType(), e.getCause());
            }
        }
        effectsToProcess.clear();
        // CraftBukkit end
    }

    @ModifyReturnValue(method = "removeAllEffects", at = @At("RETURN"))
    private boolean taiyitist$callEntityPotionEffectChangeEvent0(boolean original) {
        return removeAllEffects(EntityPotionEffectEvent.Cause.UNKNOWN);
    }

    @Inject(method = "removeEffectNoUpdate", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private void taiyitist$callEntityPotionEffectChangeEvent3(Holder<MobEffect> holder, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (isTickingEffects) {
            effectsToProcess.add(new ProcessableEffect(holder, EntityPotionEffectEvent.Cause.UNKNOWN));
            cir.setReturnValue(null);
        }

        MobEffectInstance effect = this.activeEffects.get(holder);
        if (effect == null) {
            cir.setReturnValue(null);
        }

        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(((LivingEntity) (Object) this), effect, null, EntityPotionEffectEvent.Cause.UNKNOWN);
        if (event.isCancelled()) {
            cir.setReturnValue(null);
        }
    }

    @Nullable
    @Override
    public MobEffectInstance removeEffectNoUpdate(Holder<MobEffect> holder, EntityPotionEffectEvent.Cause cause) {
        if (isTickingEffects) {
            effectsToProcess.add(new ProcessableEffect(holder, cause));
            return null;
        }

        MobEffectInstance effect = this.activeEffects.get(holder);
        if (effect == null) {
            return null;
        }

        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(((LivingEntity) (Object) this), effect, null, cause);
        if (event.isCancelled()) {
            return null;
        }
        return (MobEffectInstance)this.activeEffects.remove(holder);
    }

    @Inject(method = "removeEffect", at = @At("HEAD"))
    private void taiyitist$removeEffectCause(Holder<MobEffect> holder, CallbackInfoReturnable<Boolean> cir) {
        pushEffectCause(EntityPotionEffectEvent.Cause.UNKNOWN);
    }

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void taiyitist$UseUnscaledHealth(CallbackInfoReturnable<Float> cir) {
        // CraftBukkit start - Use unscaled health
        if (((LivingEntity) (Object) this) instanceof ServerPlayer) {
            cir.setReturnValue((float) ((ServerPlayer) (Object) this).getBukkitEntity().getHealth());
        }
        // CraftBukkit end
    }

    @Inject(method = "setHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"), cancellable = true)
    private void taiyitist$handleScaledHealth(float f, CallbackInfo ci) {
        // CraftBukkit start - Handle scaled health
        if (((LivingEntity) (Object) this) instanceof ServerPlayer) {
            org.bukkit.craftbukkit.entity.CraftPlayer player = ((ServerPlayer) (Object) this).getBukkitEntity();
            // Squeeze
            if (f < 0.0F) {
                player.setRealHealth(0.0D);
            } else if (f > player.getMaxHealth()) {
                player.setRealHealth(player.getMaxHealth());
            } else {
                player.setRealHealth(f);
            }

            player.updateScaledHealth(false);
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Override
    public boolean removeEffect(Holder<MobEffect> holder, EntityPotionEffectEvent.Cause cause) {
        MobEffectInstance mobEffectInstance = this.removeEffectNoUpdate(holder, cause);
        if (mobEffectInstance != null) {
            this.onEffectsRemoved(List.of(mobEffectInstance));
            return true;
        } else {
            return false;
        }
    }

    // CraftBukkit start
    @Override
    public boolean addEffect(MobEffectInstance mobeffect, EntityPotionEffectEvent.Cause cause) {
        return this.addEffect(mobeffect, (Entity) null, cause);
    }

    private AtomicReference<EntityRegainHealthEvent.RegainReason> taiyitist$regainReason = new AtomicReference<>(EntityRegainHealthEvent.RegainReason.CUSTOM);

    @Redirect(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void taiyitist$healEvent(LivingEntity instance, float f) {
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, this.taiyitist$regainReason.get() != null ? this.taiyitist$regainReason.get() : EntityRegainHealthEvent.RegainReason.CUSTOM);
        // Suppress during worldgen
        if (this.bridge$valid()) {
            this.level().getCraftServer().getPluginManager().callEvent(event);
        }

        if (!event.isCancelled()) {
            this.setHealth((float) (this.getHealth() + event.getAmount()));
        }
        // CraftBukkit end
    }

    @Override
    public void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
        float g = this.getHealth();
        if (g > 0.0F) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, this.taiyitist$regainReason.get() != null ? this.taiyitist$regainReason.get() : regainReason);
            // Suppress during worldgen
            if (this.bridge$valid()) {
                this.level().getCraftServer().getPluginManager().callEvent(event);
            }

            if (!event.isCancelled()) {
                this.setHealth((float) (this.getHealth() + event.getAmount()));
            }
            // CraftBukkit end
        }
    }

    @Override
    public void pushHealReason(EntityRegainHealthEvent.RegainReason reason) {
        this.taiyitist$regainReason.getAndSet(reason);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean addEffect(MobEffectInstance mobEffectInstance, @Nullable Entity entity) {
        if (isTickingEffects) {
            effectsToProcess.add(new ProcessableEffect(mobEffectInstance, EntityPotionEffectEvent.Cause.UNKNOWN));
            return true;
        }
        if (!this.canBeAffected(mobEffectInstance)) {
            return false;
        } else {
            MobEffectInstance mobEffectInstance2 = (MobEffectInstance)this.activeEffects.get(mobEffectInstance.getEffect());
            boolean bl = false;
            // CraftBukkit start
            boolean override = false;
            if (mobEffectInstance2 != null) {
                override = new MobEffectInstance(mobEffectInstance2).update(mobEffectInstance);
            }

            EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(((LivingEntity) (Object) this), mobEffectInstance2, mobEffectInstance, EntityPotionEffectEvent.Cause.UNKNOWN, override);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            if (mobEffectInstance2 == null) {
                this.activeEffects.put(mobEffectInstance.getEffect(), mobEffectInstance);
                this.onEffectAdded(mobEffectInstance, entity);
                bl = true;
                mobEffectInstance.onEffectAdded(((LivingEntity) (Object) this));
            } else if (event.isOverride()) {
                mobEffectInstance2.update(mobEffectInstance);
                this.onEffectUpdated(mobEffectInstance2, true, entity);
                // CraftBukkit end
                bl = true;
            }

            mobEffectInstance.onEffectStarted(((LivingEntity) (Object) this));
            return bl;
        }
    }

    @Override
    public boolean addEffect(MobEffectInstance mobEffectInstance, @Nullable Entity entity, EntityPotionEffectEvent.Cause cause) {
        if (isTickingEffects) {
            effectsToProcess.add(new ProcessableEffect(mobEffectInstance, cause));
            return true;
        }
        // CraftBukkit end
        if (!this.canBeAffected(mobEffectInstance)) {
            return false;
        } else {
            MobEffectInstance mobEffectInstance2 = (MobEffectInstance)this.activeEffects.get(mobEffectInstance.getEffect());
            boolean bl = false;
            // CraftBukkit start
            boolean override = false;
            if (mobEffectInstance2 != null) {
                override = new MobEffectInstance(mobEffectInstance2).update(mobEffectInstance);
            }

            EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(((LivingEntity) (Object) this), mobEffectInstance2, mobEffectInstance, cause, override);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            if (mobEffectInstance2 == null) {
                this.activeEffects.put(mobEffectInstance.getEffect(), mobEffectInstance);
                this.onEffectAdded(mobEffectInstance, entity);
                bl = true;
                mobEffectInstance.onEffectAdded(((LivingEntity) (Object) this));
            } else if (event.isOverride()) {
                mobEffectInstance2.update(mobEffectInstance);
                this.onEffectUpdated(mobEffectInstance2, true, entity);
                // CraftBukkit end
                bl = true;
            }

            mobEffectInstance.onEffectStarted(((LivingEntity) (Object) this));
            return bl;
        }
    }

    @Override
    public boolean removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        if (this.level().isClientSide) {
            return false;
        } else if (this.activeEffects.isEmpty()) {
            return false;
        } else {
            // CraftBukkit start
            List<MobEffectInstance> toRemove = new LinkedList<>();
            Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();
            while (iterator.hasNext()) {
                MobEffectInstance effect = iterator.next();
                EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(((LivingEntity) (Object) this), effect, null, cause, EntityPotionEffectEvent.Action.CLEARED);
                if (event.isCancelled()) {
                    continue;
                }

                iterator.remove();
                toRemove.add(effect);
            }

            this.onEffectsRemoved(toRemove);
            return !toRemove.isEmpty();
            // CraftBukkit end
        }
    }

    @Override
    public int getExpReward(ServerLevel handle, @Nullable Entity entity) {
        if (!this.wasExperienceConsumed() && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerMemoryTime > 0 && this.shouldDropExperience() && handle.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            int exp = this.getExperienceReward(handle, entity);
            return exp;
        } else {
            return 0;
        }
    }

    @Override
    public List<ProcessableEffect> bridge$effectsToProcess() {
        return effectsToProcess;
    }

    @Override
    public void taiyitist$setEffectsToProcess(List<ProcessableEffect> effectsToProcess) {
        this.effectsToProcess = effectsToProcess;
    }

    // CraftBukkit start - collidable API
    @Override
    public boolean canCollideWithBukkit(Entity entity) {
        return isPushable() && this.collides != this.collidableExemptions.contains(entity.getUUID());
    }
    // CraftBukkit end

    // CraftBukkit start - Add delegate methods
    @Override
    public SoundEvent getHurtSound0(DamageSource damagesource) {
        return getHurtSound(damagesource);
    }

    @Override
    public SoundEvent getDeathSound0() {
        return getDeathSound();
    }

    @Override
    public SoundEvent getFallDamageSound0(int fallHeight) {
        return getFallDamageSound(fallHeight);
    }
    // CraftBukkit end

    @Override
    public ArrayList<org.bukkit.inventory.ItemStack> bridge$drops() {
        return drops;
    }

    @Override
    public void taiyitist$setDrops(ArrayList<org.bukkit.inventory.ItemStack> drops) {
        this.drops = drops;
    }

    @Override
    public CraftAttributeMap bridge$craftAttributes() {
        return craftAttributes;
    }

    @Override
    public void taiyitist$setCraftAttributes(CraftAttributeMap craftAttributes) {
        this.craftAttributes = craftAttributes;
    }

    @Override
    public boolean bridge$collides() {
        return collides;
    }

    @Override
    public void taiyitist$setCollides(boolean collides) {
        this.collides = collides;
    }

    @Override
    public Set<UUID> bridge$collidableExemptions() {
        return collidableExemptions;
    }

    @Override
    public void taiyitist$setCollidableExemptions(Set<UUID> collidableExemptions) {
        this.collidableExemptions = collidableExemptions;
    }

    @Override
    public boolean bridge$bukkitPickUpLoot() {
        return bukkitPickUpLoot;
    }

    @Override
    public void taiyitist$setBukkitPickUpLoot(boolean bukkitPickUpLoot) {
        this.bukkitPickUpLoot = bukkitPickUpLoot;
    }

    @Override
    public boolean bridge$isTickingEffects() {
        return this.isTickingEffects;
    }

    @Override
    public void taiyitist$setIsTickingEffects(boolean isTickingEffects) {
        this.isTickingEffects = isTickingEffects;
    }

    @Override
    public int bridge$expToDrop() {
        return expToDrop;
    }

    @Override
    public void taiyitist$setExpToDrop(int expToDrop) {
        this.expToDrop = expToDrop;
    }

    @Override
    public boolean bridge$forceDrops() {
        return forceDrops;
    }

    @Override
    public void taiyitist$setForceDrops(boolean forceDrops) {
        this.forceDrops = forceDrops;
    }
}
