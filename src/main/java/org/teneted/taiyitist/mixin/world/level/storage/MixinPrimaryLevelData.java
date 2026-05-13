package org.teneted.taiyitist.mixin.world.level.storage;

import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.storage.InjectionPrimaryLevelData;

@Mixin(PrimaryLevelData.class)
public class MixinPrimaryLevelData implements InjectionPrimaryLevelData {
}
