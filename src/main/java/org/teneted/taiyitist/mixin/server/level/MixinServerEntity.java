package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionServerEntity;

@Mixin(ServerEntity.class)
public class MixinServerEntity implements InjectionServerEntity {
}
