package org.teneted.taiyitist.mixin.world.entity.animal;

import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.animal.InjectionAnimal;

@Mixin(Animal.class)
public class MixinAnimal implements InjectionAnimal {
}
