package org.teneted.taiyitist.mixin.commands;

import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.InjectionCommandSourceStack;

@Mixin(CommandSourceStack.class)
public class MixinCommandSourceStack implements InjectionCommandSourceStack {
}
