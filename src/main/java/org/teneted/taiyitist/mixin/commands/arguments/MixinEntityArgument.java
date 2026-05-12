package org.teneted.taiyitist.mixin.commands.arguments;

import net.minecraft.commands.arguments.EntityArgument;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.arguments.InjectionEntityArgument;

@Mixin(EntityArgument.class)
public class MixinEntityArgument implements InjectionEntityArgument {
}
