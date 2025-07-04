package com.taiyitistmc.mixin.world.level;

import com.taiyitistmc.injection.world.level.InjectionLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelAccessor.class)
public interface MixinLevelAccessor extends InjectionLevelAccessor {

    @Override
    ServerLevel getMinecraftWorld(); // CraftBukkit
}
