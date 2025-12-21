package org.celestial_artistry.taiyitist.mixin.world.entity;

import org.celestial_artistry.taiyitist.TaiyitistMod;
import org.celestial_artistry.taiyitist.injection.world.entity.InjectionMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Banner - TODO fix patches
@Mixin(Mob.class)
public abstract class MixinMob extends LivingEntity implements InjectionMob {

    @Shadow private boolean persistenceRequired;

    @Shadow @Nullable protected abstract SoundEvent getAmbientSound();

    @Shadow @Nullable private LivingEntity target;

    @Shadow @Nullable public abstract LivingEntity getTarget();

    @Shadow protected abstract boolean canReplaceCurrentItem(ItemStack candidate, ItemStack existing);

    @Shadow public abstract boolean canHoldItem(ItemStack stack);

    @Shadow protected abstract float getEquipmentDropChance(EquipmentSlot slot);

    @Shadow protected abstract void setItemSlotAndDropWhenKilled(EquipmentSlot slot, ItemStack stack);

    @Shadow @Nullable public abstract Entity getLeashHolder();
    @Shadow @Nullable public abstract <T extends Mob> T convertTo(EntityType<T> entityType, boolean transferInventory);

    @Unique
    public boolean aware = true; // CraftBukkit

    @Unique
    protected transient boolean taiyitist$targetSuccess = false;
    @Unique
    private transient EntityTargetEvent.TargetReason taiyitist$reason;
    @Unique
    private transient boolean taiyitist$fireEvent;
    @Unique
    private transient ItemEntity taiyitist$item;
    @Unique
    private transient EntityTransformEvent.TransformReason taiyitist$transform;

    protected MixinMob(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setCanPickUpLoot", at = @At("HEAD"))
    public void taiyitist$setPickupLoot(boolean canPickup, CallbackInfo ci) {
        super.taiyitist$setBukkitPickUpLoot(canPickup);
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public boolean canPickUpLoot() {
        return super.bridge$bukkitPickUpLoot();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(EntityType<? extends Mob> type, net.minecraft.world.level.Level worldIn, CallbackInfo ci) {
        this.aware = true;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void taiyitist$setAware(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("Bukkit.Aware", this.aware);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void taiyitist$readAware(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("Bukkit.Aware")) {
            this.aware = compound.getBoolean("Bukkit.Aware");
        }
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
    private void taiyitist$captureItemEntity(ItemEntity itemEntity, CallbackInfo ci) {
        taiyitist$item = itemEntity;
    }

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
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);            Bukkit.getPluginManager().callEvent(event);
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

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public ItemStack equipItemIfPossible(ItemStack stack) {
        ItemEntity itemEntity = taiyitist$item;
        taiyitist$item = null;
        EquipmentSlot equipmentslottype = getEquipmentSlotForItem(stack);
        ItemStack itemstack = this.getItemBySlot(equipmentslottype);
        boolean flag = this.canReplaceCurrentItem(stack, itemstack);

        if (equipmentslottype.isArmor() && !flag) {
            equipmentslottype = EquipmentSlot.MAINHAND;
            itemstack = this.getItemBySlot(equipmentslottype);
            flag = itemstack.isEmpty();
        }

        boolean canPickup = flag && this.canHoldItem(stack);
        if (itemEntity != null) {
            canPickup = !CraftEventFactory.callEntityPickupItemEvent((Mob) (Object) this, itemEntity, 0, !canPickup).isCancelled();
        }
        if (canPickup) {
            double d0 = this.getEquipmentDropChance(equipmentslottype);
            if (!itemstack.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                taiyitist$setForceDrops(true);
                this.spawnAtLocation(itemstack);
                taiyitist$setForceDrops(false);
            }

            if (equipmentslottype.isArmor() && stack.getCount() > 1) {
                ItemStack itemstack1 = stack.copyWithCount(1);
                this.setItemSlotAndDropWhenKilled(equipmentslottype, itemstack1);
                return itemstack1;
            } else {
                this.setItemSlotAndDropWhenKilled(equipmentslottype, stack);
                return stack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Inject(method = "interact", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;dropLeash(ZZ)V"))
    private void taiyitist$unleash(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (CraftEventFactory.callPlayerUnleashEntityEvent((Mob) (Object) this, player, hand).isCancelled()) {
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket((Mob) (Object) this, this.getLeashHolder()));
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "checkAndHandleImportantInteractions", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setLeashedTo(Lnet/minecraft/world/entity/Entity;Z)V"))
    private void taiyitist$leash(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (CraftEventFactory.callPlayerLeashEntityEvent((Mob) (Object) this, player, player, hand).isCancelled()) {
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket((Mob) (Object) this, this.getLeashHolder()));
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "tickLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;dropLeash(ZZ)V"))
    public void taiyitist$unleash2(CallbackInfo ci) {
        Bukkit.getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), this.isAlive() ?
                EntityUnleashEvent.UnleashReason.HOLDER_GONE : EntityUnleashEvent.UnleashReason.PLAYER_UNLEASH));
    }

    @Inject(method = "dropLeash", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    public void taiyitist$leashDropPost(boolean sendPacket, boolean dropLead, CallbackInfo ci) {
        this.taiyitist$setForceDrops(false);
    }

    @Inject(method = "dropLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    public void taiyitist$leashDropPre(boolean sendPacket, boolean dropLead, CallbackInfo ci) {
        this.taiyitist$setForceDrops(true);
    }

    @Inject(method = "restoreLeashFromSave", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void taiyitist$leashRestorePost(CallbackInfo ci) {
        this.taiyitist$setForceDrops(false);
    }

    @Inject(method = "restoreLeashFromSave", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void taiyitist$leashRestorePre(CallbackInfo ci) {
        this.taiyitist$setForceDrops(true);
    }

    @Inject(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;dropLeash(ZZ)V"))
    private void taiyitist$unleashRide(Entity entityIn, boolean force, CallbackInfoReturnable<Boolean> cir) {
        Bukkit.getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.UNKNOWN));
    }

    @Inject(method = "removeAfterChangingDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;dropLeash(ZZ)V"))
    private void taiyitist$unleashDead(CallbackInfo ci) {
        Bukkit.getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.UNKNOWN));
    }

    @Inject(method = "convertTo", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void taiyitist$copySpawn(EntityType<?> entityType, boolean transferInventory, CallbackInfoReturnable<?> cir) {
        EntityTransformEvent.TransformReason transformReason = taiyitist$transform == null ? EntityTransformEvent.TransformReason.UNKNOWN : taiyitist$transform;
        taiyitist$transform = null;
        if (CraftEventFactory.callEntityTransformEvent((Mob) (Object) this, (Mob)entityType.create(this.level()), transformReason).isCancelled()) {
            cir.setReturnValue(null);
        }
    }

    @Redirect(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSecondsOnFire(I)V"))
    public void taiyitist$attackCombust(Entity entity, int seconds) {
        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), seconds);
        org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);
        if (!combustEvent.isCancelled()) {
            entity.setSecondsOnFire(combustEvent.getDuration(), false);
        }
    }

    @Mixin(Mob.class)
    public abstract static class PaperSpawnAffect extends LivingEntity {

        protected PaperSpawnAffect(EntityType<? extends LivingEntity> entityType, Level level) {
            super(entityType, level);
        }
    }

    @Override
    public <T extends Mob> T convertTo(EntityType<T> entitytypes, boolean flag, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        this.level().pushAddEntityReason(spawnReason);
        bridge$pushTransformReason(transformReason);
        return this.convertTo(entitytypes, flag);
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
}
