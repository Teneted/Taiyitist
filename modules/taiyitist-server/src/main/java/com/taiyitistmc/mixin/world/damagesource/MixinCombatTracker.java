package com.taiyitistmc.mixin.world.damagesource;

import com.taiyitistmc.injection.world.damagesource.InjectionCombatTracker;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatTracker.class)
public class MixinCombatTracker implements InjectionCombatTracker {

    @Shadow
    @Final
    private List<CombatEntry> entries;

    private Component taiyitist$emptyComnent;

    @Inject(method = "getDeathMessage", cancellable = true, at = @At("HEAD"))
    private void taiyitist$useOverride(CallbackInfoReturnable<Component> cir) {
        if (!this.entries.isEmpty()) {
            var entry = this.entries.get(this.entries.size() - 1);
            var deathMessage = entry.bridge$deathMessage();
            if (deathMessage != null) {
                cir.setReturnValue(deathMessage);
            }
        } else {
            if (this.taiyitist$emptyComnent != null) {
                cir.setReturnValue(this.taiyitist$emptyComnent);
            }
        }
        this.taiyitist$emptyComnent = null;
    }

    @Override
    public void taiyitist$setDeathMessage(Component component) {
        this.taiyitist$emptyComnent = component;
        if (!this.entries.isEmpty()) {
            var entry = this.entries.get(this.entries.size() - 1);
            entry.taiyitist$setDeathMessage(component);
        }
    }
}
