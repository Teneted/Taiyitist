package org.teneted.taiyitist.mixin.network.syncher;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.syncher.InjectionSynchedEntityData;

@Mixin(SynchedEntityData.class)
public class MixinSynchedEntityData implements InjectionSynchedEntityData {
}
