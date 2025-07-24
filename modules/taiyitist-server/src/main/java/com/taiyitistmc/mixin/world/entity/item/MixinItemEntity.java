package com.taiyitistmc.mixin.world.entity.item;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.BukkitFieldHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements TraceableEntity {

    @Shadow public abstract ItemStack getItem();

    @Shadow private int pickupDelay;

    @Shadow protected abstract void setUnderwaterMovement();

    @Shadow protected abstract void setUnderLavaMovement();

    @Shadow protected abstract void mergeWithNeighbours();

    @Shadow private int age;

    @Shadow protected abstract boolean isMergable();

    private int lastTick = BukkitFieldHooks.currentTick() - 1; // CraftBukkit

    public MixinItemEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
            this.discard();
        } else {
            super.tick();
            // CraftBukkit start - Use wall time for pickup and despawn timers
            int elapsedTicks = BukkitFieldHooks.currentTick() - this.lastTick;
            if (this.pickupDelay != 32767) this.pickupDelay -= elapsedTicks;
            if (this.age != -32768) this.age += elapsedTicks;
            this.lastTick = BukkitFieldHooks.currentTick();
            // CraftBukkit end

            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > 0.10000000149011612) {
                this.setUnderwaterMovement();
            } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > 0.10000000149011612) {
                this.setUnderLavaMovement();
            } else {
                this.applyGravity();
            }

            if (this.level().isClientSide) {
                this.noPhysics = false;
            } else {
                this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7));
                if (this.noPhysics) {
                    this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                }
            }

            if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > 9.999999747378752E-6 || (this.tickCount + this.getId()) % 4 == 0) {
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.applyEffectsFromBlocks();
                float f = 0.98F;
                if (this.onGround()) {
                    f = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.98F;
                }

                this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.98, (double)f));
                if (this.onGround()) {
                    Vec3 vec32 = this.getDeltaMovement();
                    if (vec32.y < 0.0) {
                        this.setDeltaMovement(vec32.multiply(1.0, -0.5, 1.0));
                    }
                }
            }

            boolean bl = Mth.floor(this.xo) != Mth.floor(this.getX()) || Mth.floor(this.yo) != Mth.floor(this.getY()) || Mth.floor(this.zo) != Mth.floor(this.getZ());
            int i = bl ? 2 : 40;
            if (this.tickCount % i == 0 && !this.level().isClientSide && this.isMergable()) {
                this.mergeWithNeighbours();
            }

            /* CraftBukkit start - moved up
            if (this.age != -32768) {
                ++this.age;
            }
            // CraftBukkit end */

            this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
            if (!this.level().isClientSide) {
                double d = this.getDeltaMovement().subtract(vec3).lengthSqr();
                if (d > 0.01) {
                    this.hasImpulse = true;
                }
            }

            if (!this.level().isClientSide && this.age >= this.level().bridge$spigotConfig().itemDespawnRate) { // Spigot
                // CraftBukkit start - fire ItemDespawnEvent
                if (CraftEventFactory.callItemDespawnEvent(((ItemEntity) (Object) this)).isCancelled()) {
                    this.age = 0;
                    return;
                }
                // CraftBukkit end
                this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);// CraftBukkit - add Bukkit remove cause
                this.discard();
            }
        }
    }

    @ModifyConstant(method = "makeFakeItem", constant = @Constant(intValue = 5999))
    private int taiyitist$modifyItemMerge(int constant) {
        return this.level().bridge$spigotConfig().itemDespawnRate - 1; // Spigot
    }

    @Inject(method = "merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void taiyitist$mergeEvent(ItemEntity itemEntity, ItemStack itemStack, ItemEntity itemEntity2, ItemStack itemStack2, CallbackInfo ci) {
        // CraftBukkit start
        if (!CraftEventFactory.callItemMergeEvent(itemEntity2, itemEntity)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;markHurt()V"), cancellable = true)
    private void taiyitist$handleNonLivingEntityDamageEvent(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, damageSource, f)) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void taiyitist$removeCause(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DEATH); // CraftBukkit - add Bukkit remove cause
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void taiyitist$removeCause0(ValueInput valueInput, CallbackInfo ci) {
        this.pushRemoveCause(null); // CraftBukkit - add Bukkit remove cause
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void taiyitist$removeCause1(Player player, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP); // CraftBukkit - add Bukkit remove cause
    }

    @Inject(method = "playerTouch", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/item/ItemEntity;pickupDelay:I"), cancellable = true)
    private void taiyitist$firePlayerPickupItemEvent(Player entityhuman, CallbackInfo ci, @Local ItemStack itemstack, @Local int i) {
        // CraftBukkit start - fire PlayerPickupItemEvent
        int canHold = entityhuman.getInventory().canHold(itemstack);
        int remaining = i - canHold;

        if (this.pickupDelay <= 0 && canHold > 0) {
            itemstack.setCount(canHold);
            // Call legacy event
            PlayerPickupItemEvent playerEvent = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), (org.bukkit.entity.Item) this.getBukkitEntity(), remaining);
            playerEvent.setCancelled(!playerEvent.getPlayer().getCanPickupItems());
            this.level().getCraftServer().getPluginManager().callEvent(playerEvent);
            if (playerEvent.isCancelled()) {
                itemstack.setCount(i); // SPIGOT-5294 - restore count
                ci.cancel();
                return;
            }

            // Call newer event afterwards
            EntityPickupItemEvent entityEvent = new EntityPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), (org.bukkit.entity.Item) this.getBukkitEntity(), remaining);
            entityEvent.setCancelled(!entityEvent.getEntity().getCanPickupItems());
            this.level().getCraftServer().getPluginManager().callEvent(entityEvent);
            if (entityEvent.isCancelled()) {
                itemstack.setCount(i); // SPIGOT-5294 - restore count
                ci.cancel();
                return;
            }

            // Update the ItemStack if it was changed in the event
            ItemStack current = this.getItem();
            if (!itemstack.equals(current)) {
                itemstack = current;
            } else {
                itemstack.setCount(canHold + remaining); // = i
            }

            // Possibly < 0; fix here so we do not have to modify code below
            this.pickupDelay = 0;
        } else if (this.pickupDelay == 0) {
            // ensure that the code below isn't triggered if canHold says we can't pick the items up
            this.pickupDelay = -1;
        }
        // CraftBukkit end
    }

    @WrapWithCondition(method = "tryToMerge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    private boolean taiyitist$checkTrue(ItemEntity itemEntity, ItemStack itemStack, ItemEntity itemEntity2, ItemStack itemStack2) {
        return true || itemStack2.getCount() < itemStack.getCount();
    }

    @ModifyArgs(method = "mergeWithNeighbours", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"))
    private void taiyitist$itemMergeConfigure(Args args) {
        double radius = this.level().bridge$spigotConfig().itemMerge;
        args.set(0, radius);
        args.set(1, radius - 0.5D);
        args.set(2, radius);
    }
}
