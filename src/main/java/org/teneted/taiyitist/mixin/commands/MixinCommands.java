package org.teneted.taiyitist.mixin.commands;

import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.InjectionCommands;

@Mixin(Commands.class)
public class MixinCommands implements InjectionCommands {
}
