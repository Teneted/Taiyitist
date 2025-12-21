package org.celestial_artistry.taiyitist.mixin.world.level;

import org.celestial_artistry.taiyitist.injection.world.level.InjectionLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelAccessor.class)
public interface MixinLevelAccessor extends InjectionLevelAccessor {

    @Override
    ServerLevel getMinecraftWorld(); // CraftBukkit
}
