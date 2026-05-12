package org.teneted.taiyitist.mixin.advancements;

import net.minecraft.advancements.AdvancementHolder;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.advancements.InjectionAdvancementHolder;

@Mixin(AdvancementHolder.class)
public class MixinAdvancementHolder implements InjectionAdvancementHolder {

    // CraftBukkit start
    @Override
    public final org.bukkit.advancement.Advancement toBukkit() {
        return new CraftAdvancement(((AdvancementHolder) (Object) this));
    }
    // CraftBukkit end
}
