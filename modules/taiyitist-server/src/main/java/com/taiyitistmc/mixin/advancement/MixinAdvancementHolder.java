package com.taiyitistmc.mixin.advancement;

import com.taiyitistmc.injection.advancements.InjectionAdvancementHolder;
import net.minecraft.advancements.AdvancementHolder;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AdvancementHolder.class)
public class MixinAdvancementHolder implements InjectionAdvancementHolder {

    @Override
    public Advancement toBukkit() {
        return new CraftAdvancement(((AdvancementHolder) (Object) this));
    }
}