package com.taiyitistmc.mixin.world.entity.player;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.taiyitistmc.injection.world.entity.player.InjectionPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Taiyitist - TODO fix mixins
@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity implements InjectionPlayer {

    @Shadow protected PlayerEnderChestContainer enderChestInventory;

    @Shadow protected FoodData foodData;

    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(CallbackInfo ci) {
        this.foodData.setEntityhuman((net.minecraft.world.entity.player.Player) (Object) this);
        this.enderChestInventory.setOwner(this.getBukkitEntity());
    }

    // CraftBukkit start
    public boolean fauxSleeping;
    public int oldLevel = -1;

    @Override
    public CraftHumanEntity getBukkitEntity() {
        return (CraftHumanEntity) super.getBukkitEntity();
    }
    // CraftBukkit end

    @Inject(method = "turtleHelmetTick", at = @At("HEAD"))
    private void taiyitist$pushTurtleHelmetTickReason(CallbackInfo ci) {
        this.pushEffectCause(EntityPotionEffectEvent.Cause.TURTLE_HELMET);
    }

    @WrapWithCondition(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setShiftKeyDown(Z)V"))
    private boolean taiyitist$checkPassenger(Player instance, boolean b) {
        return !this.isPassenger(); // CraftBukkit start - SPIGOT-7316: no longer passenger, dismount and return
    }

    @Inject(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setShiftKeyDown(Z)V", shift = At.Shift.AFTER), cancellable = true)
    private void taiyitist$cancelWhenNotPassenger(CallbackInfo ci) {
        ci.cancel();
    }

    private boolean respawnEntityOnShoulder(CompoundTag nbttagcompound) { // CraftBukkit void->boolean
        Entity entity = getEntityOnShoulder(nbttagcompound);
        if (entity != null) {
            return ((ServerLevel) this.level()).addWithUUID(entity, CreatureSpawnEvent.SpawnReason.SHOULDER_ENTITY);
        }

        return true;
        // CraftBukkit end
    }

    @Override
    public int bridge$oldLevel() {
        return oldLevel;
    }

    @Override
    public boolean bridge$fauxSleeping() {
        return fauxSleeping;
    }

    @Override
    public void taiyitist$setFauxSleeping(boolean fauxSleeping) {
        this.fauxSleeping = fauxSleeping;
    }

    @Override
    public void taiyitist$setOldLevel(int oldLevel) {
        this.oldLevel = oldLevel;
    }
}
