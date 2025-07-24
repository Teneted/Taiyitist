package com.taiyitistmc.mixin.world.entity.projectile;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class MixinAbstractArrow extends Projectile {

    @Shadow
    public abstract boolean isInGround();

    @Shadow
    public int life;

    @Shadow public abstract boolean isNoPhysics();

    @Shadow public int shakeTime;

    @Shadow protected abstract boolean tryPickup(Player player);

    @Shadow public AbstractArrow.Pickup pickup;

    @Shadow protected abstract ItemStack getPickupItem();

    @ShadowConstructor
    public void taiyitist$constructor(EntityType<? extends AbstractArrow> entityType, double d, double e, double f, Level level, ItemStack itemStack, @Nullable ItemStack itemStack2) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(EntityType<? extends AbstractArrow> entityType, double d, double e, double f, Level level, ItemStack itemStack, @Nullable ItemStack itemStack2, @Nullable LivingEntity ownerEntity) {
        taiyitist$constructor(entityType, d, e, f, level, itemStack, itemStack2);
        this.setOwner(ownerEntity);
        // CraftBukkit end
    }

    public MixinAbstractArrow(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "stepMoveAndHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection taiyitist$resetHit(AbstractArrow instance, HitResult hitResult) {
        return this.preHitTargetOrDeflectSelf(hitResult); // CraftBukkit - projectile hit event
    }

    @Inject(method = "tickDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V"))
    private void taiyitist$discardCause(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN); // CraftBukkit - add Bukkit remove cause
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V"))
    private void taiyitist$discardCause0(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT); // CraftBukkit - add Bukkit remove cause
    }

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void taiyitist$fireCombustEvent(Entity entity, float f) {
        // CraftBukkit start
        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 5.0F);
        org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);
        if (!combustEvent.isCancelled()) {
            entity.igniteForSeconds(combustEvent.getDuration(), false);
        }
        // CraftBukkit end
    }

    @Override
    public void inactiveTick(){
        if (this.isInGround()){
            this.life += 1;
        }
        super.inactiveTick();
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public void playerTouch(Player entityhuman) {
        if (!this.level().isClientSide && (this.isInGround() || this.isNoPhysics()) && this.shakeTime <= 0) {
            // CraftBukkit start
            ItemStack itemstack = this.getPickupItem();
            if (this.pickup == AbstractArrow.Pickup.ALLOWED && !itemstack.isEmpty() && entityhuman.getInventory().canHold(itemstack) > 0) {
                ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemstack);
                PlayerPickupArrowEvent event = new PlayerPickupArrowEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), new org.bukkit.craftbukkit.entity.CraftItem(this.level().getCraftServer(), item), (org.bukkit.entity.AbstractArrow) this.getBukkitEntity());
                // event.setCancelled(!entityhuman.canPickUpLoot); TODO
                this.level().getCraftServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
                itemstack = item.getItem();
            }

            if ((this.pickup == AbstractArrow.Pickup.ALLOWED && entityhuman.getInventory().add(itemstack)) || (this.pickup == AbstractArrow.Pickup.CREATIVE_ONLY && entityhuman.getAbilities().instabuild)) {
                // CraftBukkit end
                entityhuman.take(this, 1);
                this.discard();
            }

        }
    }
}
