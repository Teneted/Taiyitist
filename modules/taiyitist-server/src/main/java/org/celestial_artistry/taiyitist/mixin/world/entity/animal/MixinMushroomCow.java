package org.celestial_artistry.taiyitist.mixin.world.entity.animal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MushroomCow.class)
public abstract class MixinMushroomCow extends Animal {

    protected MixinMushroomCow(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", require = 0, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/MushroomCow;shear(Lnet/minecraft/sounds/SoundSource;)V"))
    private void taiyitist$shear(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, ItemStack stack) {
        if (!CraftEventFactory.handlePlayerShearEntityEvent(player, (Entity) (Object) this, stack, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
