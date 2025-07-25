package com.taiyitistmc.mixin.world.entity.animal.horse;

import com.taiyitistmc.injection.world.entity.InjectionAbstractHorse;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends Animal implements HasCustomInventoryScreen, OwnableEntity, PlayerRideableJumping, InjectionAbstractHorse {

    public int maxDomestication = 100; // CraftBukkit - store max domestication value

    protected MixinAbstractHorse(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(EntityType entityType, Level level, CallbackInfo ci) {
        this.maxDomestication = 100;
    }

    @Redirect(method = "createInventory", at = @At(value = "NEW", args = "class=net/minecraft/world/SimpleContainer"))
    private SimpleContainer taiyitist$createInv(int slots) {
        SimpleContainer inventory = new SimpleContainer(slots);
        inventory.setOwner((InventoryHolder) this.getBukkitEntity());
        return inventory;
    }

    @Inject(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;heal(F)V"))
    private void taiyitist$healByEating(Player player, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        pushHealReason(EntityRegainHealthEvent.RegainReason.EATING);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;heal(F)V"))
    private void taiyitist$healByRegen(CallbackInfo ci) {
        pushHealReason(EntityRegainHealthEvent.RegainReason.REGEN);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$writeTemper(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt("Bukkit.MaxDomestication", this.maxDomestication);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$readTemper(ValueInput valueInput, CallbackInfo ci) {
        // CraftBukkit start
        this.maxDomestication = valueInput.getIntOr("Bukkit.MaxDomestication", this.maxDomestication);
        // CraftBukkit end
    }

    @Inject(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;heal(F)V"))
    private void taiyitist$pushHealReason(Player player, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        this.pushHealReason(EntityRegainHealthEvent.RegainReason.EATING); // CraftBukkit
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;heal(F)V"))
    private void taiyitist$pushHealReason0(CallbackInfo ci) {
        this.pushHealReason(EntityRegainHealthEvent.RegainReason.REGEN); // CraftBukkit
    }

    @Inject(method = "handleStartJump", cancellable = true, at = @At("HEAD"))
    private void taiyitist$horseJump(int i, CallbackInfo ci) {
        float power;
        if (i >= 90) {
            power = 1.0F;
        } else {
            power = 0.4F + 0.4F * (float) i / 90.0F;
        }
        if (!CraftEventFactory.callHorseJumpEvent((AbstractHorse) (Object) this, power)) {
            ci.cancel();
        }
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public int getMaxTemper() {
        return maxDomestication;
    }

    @Override
    public int bridge$maxDomestication() {
        return this.maxDomestication;
    }

    @Override
    public void taiyitist$setMaxDomestication(int maxDomestication) {
        this.maxDomestication = maxDomestication;
    }
}
