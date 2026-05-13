package org.teneted.taiyitist.mixin.world.level.entity;

import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.entity.InjectionPersistentEntitySectionManager;

@Mixin(PersistentEntitySectionManager.class)
public class MixinPersistentEntitySectionManager implements InjectionPersistentEntitySectionManager {
}
