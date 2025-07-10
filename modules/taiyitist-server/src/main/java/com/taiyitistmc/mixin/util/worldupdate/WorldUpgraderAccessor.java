package com.taiyitistmc.mixin.util.worldupdate;

import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(WorldUpgrader.class)
public interface WorldUpgraderAccessor {
    @Accessor("levels")
    void setLevels(Set<ResourceKey<Level>> levels);
}
