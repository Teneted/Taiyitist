package com.taiyitistmc.mixin.world.damagesource;

import com.taiyitistmc.injection.world.damagesource.InjectionCombatEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatEntry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CombatEntry.class)
public class MixinCombatEntry implements InjectionCombatEntry {

    private Component taiyitist$deathMessage;

    @Override
    public void taiyitist$setDeathMessage(Component component) {
        this.taiyitist$deathMessage = component;
    }

    @Override
    public Component bridge$deathMessage() {
        return this.taiyitist$deathMessage;
    }
}
