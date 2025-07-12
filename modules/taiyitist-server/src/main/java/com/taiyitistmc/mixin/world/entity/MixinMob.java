package com.taiyitistmc.mixin.world.entity;

import com.taiyitistmc.TaiyitistMod;
import com.taiyitistmc.injection.world.entity.InjectionMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Taiyitist - TODO fix patches
@Mixin(Mob.class)
public abstract class MixinMob extends LivingEntity implements InjectionMob {

    public boolean aware = true; // CraftBukkit
    protected transient boolean taiyitist$targetSuccess = false;
    @Shadow
    private boolean persistenceRequired;
    @Shadow
    @Nullable
    private LivingEntity target;
    @Shadow
    @Nullable
    private Leashable.LeashData leashData;
    private transient EntityTargetEvent.TargetReason taiyitist$reason;
    private transient boolean taiyitist$fireEvent;
    private transient ItemEntity taiyitist$item;
    private transient EntityTransformEvent.TransformReason taiyitist$transform;

    protected MixinMob(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    @Nullable
    protected abstract SoundEvent getAmbientSound();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public void setTarget(@Nullable LivingEntity livingEntity) {
        boolean fireEvent = taiyitist$fireEvent;
        taiyitist$fireEvent = false;
        EntityTargetEvent.TargetReason reason = taiyitist$reason == null ? EntityTargetEvent.TargetReason.UNKNOWN : taiyitist$reason;
        taiyitist$reason = null;
        if (getTarget() == livingEntity) {
            taiyitist$targetSuccess = false;
            return;
        }
        if (fireEvent) {
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN && this.getTarget() != null && livingEntity == null) {
                reason = (this.getTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED);
            }
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                TaiyitistMod.LOGGER.warn("Unknown target reason setting {} target to {}", this, livingEntity);
            }
            CraftLivingEntity ctarget = null;
            if (livingEntity != null) {
                ctarget = (CraftLivingEntity) livingEntity.getBukkitEntity();
            }
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);
            Bukkit.getPluginManager().callEvent(event);
            level().getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                taiyitist$targetSuccess = false;
                return;
            }
            if (event.getTarget() != null) {
                livingEntity = ((CraftLivingEntity) event.getTarget()).getHandle();
            } else {
                livingEntity = null;
            }
        }
        this.target = livingEntity;
        taiyitist$targetSuccess = true;
    }

    @Shadow
    public abstract boolean canHoldItem(ItemStack stack);
    @Shadow
    protected abstract void setItemSlotAndDropWhenKilled(EquipmentSlot slot, ItemStack stack);

    @Inject(method = "setCanPickUpLoot", at = @At("HEAD"))
    public void taiyitist$setPickupLoot(boolean canPickup, CallbackInfo ci) {
        super.taiyitist$setBukkitPickUpLoot(canPickup);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean canPickUpLoot() {
        return super.bridge$bukkitPickUpLoot();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(EntityType<? extends Mob> type, Level worldIn, CallbackInfo ci) {
        this.aware = true;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void taiyitist$setAware(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putBoolean("Bukkit.Aware", this.aware);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void taiyitist$readAware(ValueInput valueInput, CallbackInfo ci) {
        this.aware = valueInput.getBooleanOr("Bukkit.Aware", this.aware);
    }

    @Redirect(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setCanPickUpLoot(Z)V"))
    public void taiyitist$setIfTrue(Mob mobEntity, boolean canPickup) {
        if (canPickup) mobEntity.setCanPickUpLoot(true);
    }

    @Inject(method = "serverAiStep", cancellable = true, at = @At("HEAD"))
    private void taiyitist$unaware(CallbackInfo ci) {
        if (!this.aware) {
            ++this.noActionTime;
            ci.cancel();
        }
    }

    @Inject(method = "pickUpItem", at = @At("HEAD"))
    private void taiyitist$captureItemEntity(ServerLevel serverLevel, ItemEntity itemEntity, CallbackInfo ci) {
        taiyitist$item = itemEntity;
    }


    // Banner TODO fixme
    @Inject(method = "interact", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkAndHandleImportantInteractions(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void taiyitist$unleash(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (CraftEventFactory.callPlayerUnleashEntityEvent((Mob) (Object) this, player, hand).isCancelled() && this.leashData != null) {
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket((Mob) (Object) this, this.leashData.leashHolder));
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Override
    public void bridge$pushTransformReason(EntityTransformEvent.TransformReason transformReason) {
        this.taiyitist$transform = transformReason;
    }

    @Override
    public boolean setTarget(LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        bridge$pushGoalTargetReason(reason, fireEvent);
        setTarget(entityliving);
        return taiyitist$targetSuccess;
    }

    @Override
    public void bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (fireEvent) {
            this.taiyitist$reason = reason;
        } else {
            this.taiyitist$reason = null;
        }
        taiyitist$fireEvent = fireEvent;
    }

    @Override
    public SoundEvent getAmbientSound0() {
        return getAmbientSound();
    }

    @Override
    public void setPersistenceRequired(boolean persistenceRequired) {
        this.persistenceRequired = persistenceRequired;
    }

    @Override
    public boolean bridge$aware() {
        return aware;
    }

    @Override
    public void taiyitist$setAware(boolean aware) {
        this.aware = aware;
    }

    @Override
    public boolean gettaiyitist$targetSuccess() {
        return taiyitist$targetSuccess;
    }

    @Mixin(Mob.class)
    public abstract static class PaperSpawnAffect extends LivingEntity {

        protected PaperSpawnAffect(EntityType<? extends LivingEntity> entityType, Level level) {
            super(entityType, level);
        }
    }
}