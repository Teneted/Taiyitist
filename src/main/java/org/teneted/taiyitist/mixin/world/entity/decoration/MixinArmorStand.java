package org.teneted.taiyitist.mixin.world.entity.decoration;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.decoration.InjectionArmorStand;

@Mixin(ArmorStand.class)
public class MixinArmorStand implements InjectionArmorStand {
}
