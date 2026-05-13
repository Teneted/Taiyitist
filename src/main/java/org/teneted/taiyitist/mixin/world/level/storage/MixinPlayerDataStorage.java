package org.teneted.taiyitist.mixin.world.level.storage;

import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.storage.InjectionPlayerDataStorage;

@Mixin(PlayerDataStorage.class)
public class MixinPlayerDataStorage implements InjectionPlayerDataStorage {
}
