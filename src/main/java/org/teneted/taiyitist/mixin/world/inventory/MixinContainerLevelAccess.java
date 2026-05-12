package org.teneted.taiyitist.mixin.world.inventory;

import net.minecraft.world.inventory.ContainerLevelAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.inventory.InjectionContainerLevelAccess;

@Mixin(ContainerLevelAccess.class)
public class MixinContainerLevelAccess implements InjectionContainerLevelAccess {
}
