package org.teneted.taiyitist.mixin.world.level.spawner;

import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.spawner.InjectionSpawnState;

@Mixin(NaturalSpawner.SpawnState.class)
public class MixinSpawnState implements InjectionSpawnState {
}
